package com.example.ui.dzikir

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.DzikirItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DzikirViewModel(application: Application) : AndroidViewModel(application) {

    private val vibrator = application.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator

    private val _dzikirList = MutableStateFlow(
        listOf(
            DzikirItem(
                id = 1,
                title = "Tasbih",
                arabic = "سُبْحَانَ اللَّهِ",
                latin = "Subhanallah",
                translation = "Mahasuci Allah",
                countTarget = 33,
                currentCount = 0,
                benefit = "Menghapus dosa-dosa dan menenangkan hati",
                category = "Sholat"
            ),
            DzikirItem(
                id = 2,
                title = "Tahmid",
                arabic = "الْحَمْدُ لِلَّهِ",
                latin = "Alhamdulillah",
                translation = "Segala puji bagi Allah",
                countTarget = 33,
                currentCount = 0,
                benefit = "Memenuhi timbangan amal kebaikan",
                category = "Sholat"
            ),
            DzikirItem(
                id = 3,
                title = "Takbir",
                arabic = "اللَّهُ أَكْبَرُ",
                latin = "Allahu Akbar",
                translation = "Allah Mahabesar",
                countTarget = 33,
                currentCount = 0,
                benefit = "Mengagungkan kebesaran Allah",
                category = "Sholat"
            ),
            DzikirItem(
                id = 4,
                title = "Tahlil",
                arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                latin = "Laa ilaaha illallaah wahdahu laa syariikalah...",
                translation = "Tiada tuhan selain Allah yang Maha Esa, tidak ada sekutu bagi-Nya...",
                countTarget = 10,
                currentCount = 0,
                benefit = "Dzikir penutup sholat yang paling utama",
                category = "Sholat"
            ),
            DzikirItem(
                id = 5,
                title = "Istighfar",
                arabic = "أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ",
                latin = "Astaghfirullaahal-'azhiim wa atuubu ilaih",
                translation = "Aku memohon ampun kepada Allah Yang Maha Agung dan aku bertaubat kepada-Nya",
                countTarget = 100,
                currentCount = 0,
                benefit = "Membuka pintu rezeki dan menghapus kegelisahan",
                category = "Pagi"
            )
        )
    )
    val dzikirList: StateFlow<List<DzikirItem>> = _dzikirList.asStateFlow()

    private val _selectedDzikirIndex = MutableStateFlow(0)
    val selectedDzikirIndex: StateFlow<Int> = _selectedDzikirIndex.asStateFlow()

    fun selectDzikir(index: Int) {
        _selectedDzikirIndex.value = index
    }

    fun incrementCount() {
        val list = _dzikirList.value.toMutableList()
        val index = _selectedDzikirIndex.value
        val item = list[index]

        val newCount = item.currentCount + 1
        list[index] = item.copy(currentCount = newCount)
        _dzikirList.value = list

        triggerHaptic(isCompleted = newCount >= item.countTarget)
    }

    fun resetCount() {
        val list = _dzikirList.value.toMutableList()
        val index = _selectedDzikirIndex.value
        list[index] = list[index].copy(currentCount = 0)
        _dzikirList.value = list
    }

    private fun triggerHaptic(isCompleted: Boolean) {
        try {
            if (isCompleted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1))
                } else {
                    vibrator?.vibrate(200)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator?.vibrate(30)
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
    }
}
