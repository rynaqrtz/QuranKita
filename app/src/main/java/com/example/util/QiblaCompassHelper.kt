package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class CompassState(
    val deviceAzimuth: Float = 0f,
    val qiblaBearing: Float = 295f, // Default approx for Indonesia ~295°
    val qiblaRelativeAngle: Float = 0f,
    val isAlignedWithQibla: Boolean = false,
    val distanceToMakkahKm: Int = 7900,
    val isSensorAvailable: Boolean = true
)

class QiblaCompassHelper(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _compassState = MutableStateFlow(CompassState())
    val compassState: StateFlow<CompassState> = _compassState.asStateFlow()

    private var currentLat: Double = -6.2088 // Default Jakarta
    private var currentLng: Double = 106.8456

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    fun setLocation(lat: Double, lng: Double) {
        currentLat = lat
        currentLng = lng
        updateQiblaBearing()
    }

    private fun updateQiblaBearing() {
        val kaabaLat = Math.toRadians(21.422487)
        val kaabaLng = Math.toRadians(39.826206)
        val userLat = Math.toRadians(currentLat)
        val userLng = Math.toRadians(currentLng)

        val deltaLng = kaabaLng - userLng
        val y = sin(deltaLng) * cos(kaabaLat)
        val x = cos(userLat) * sin(kaabaLat) - sin(userLat) * cos(kaabaLat) * cos(deltaLng)

        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
        bearing = (bearing + 360f) % 360f

        // Great circle distance to Makkah (Haversine)
        val earthRadiusKm = 6371.0
        val dLat = kaabaLat - userLat
        val dLng = deltaLng
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(userLat) * cos(kaabaLat) * sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distanceKm = (earthRadiusKm * c).toInt()

        _compassState.value = _compassState.value.copy(
            qiblaBearing = bearing,
            distanceToMakkahKm = distanceKm
        )
    }

    fun startListening() {
        val hasSensor = if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else if (accelerometer != null && magnetometer != null) {
            val aOk = sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            val mOk = sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
            aOk && mOk
        } else {
            false
        }

        if (!hasSensor) {
            _compassState.value = _compassState.value.copy(isSensorAvailable = false)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        var azimuth = 0f

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            azimuth = (azimuth + 360f) % 360f
        } else {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, gravity, 0, 3)
                hasGravity = true
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                hasGeomagnetic = true
            }

            if (hasGravity && hasGeomagnetic) {
                val r = FloatArray(9)
                val i = FloatArray(9)
                if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(r, orientation)
                    azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    azimuth = (azimuth + 360f) % 360f
                }
            }
        }

        val qiblaBearing = _compassState.value.qiblaBearing
        val relative = (qiblaBearing - azimuth + 360f) % 360f
        val isAligned = kotlin.math.abs(relative) < 4f || kotlin.math.abs(relative - 360f) < 4f

        _compassState.value = _compassState.value.copy(
            deviceAzimuth = azimuth,
            qiblaRelativeAngle = relative,
            isAlignedWithQibla = isAligned,
            isSensorAvailable = true
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
