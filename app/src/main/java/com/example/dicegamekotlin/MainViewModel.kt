package com.example.dicegamekotlin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class MainViewModel : ViewModel() {
    private val _dices = mutableListOf<MutableLiveData<Int>>();
    private val _isActiveRotate = MutableLiveData<Boolean>(true);

    val dices: List<LiveData<Int>> = _dices;
    val isActiveRotate: LiveData<Boolean> = _isActiveRotate

    fun doRotate() {
        viewModelScope.launch {
            _isActiveRotate.value = false
            withContext(Dispatchers.Main) {
                _dices.map {
                    launch {
                        doRotate(it)
                    }
                }.joinAll()

                _isActiveRotate.value = true
            }

        }
    }

    private suspend fun doRotate(dice: MutableLiveData<Int>): Int {
        val numberCycle = ThreadLocalRandom.current()
            .nextInt(MainActivity.MAX_CYCLE - MainActivity.MIN_CYCLE + 1) + MainActivity.MIN_CYCLE
//        val numberCycle = MAX_CYCLE
        val random = Random(System.currentTimeMillis())
        var i = 1
        var resultResId: Int = 0
        while (i < numberCycle) {
            resultResId = random.nextInt(6)
            delay((10 * i).toLong())
            dice.value = resultResId
            i++
        }

        return resultResId;
    }

    fun init(size: Int) {
        _dices.clear();
        (1..size).forEach { _dices.add((MutableLiveData<Int>())) }
    }

    fun setDiceValue(index: Int, value: Int) {
        _dices[index].value = value;
    }

    fun getDiceValue(index: Int): Int? {
        return _dices[index].value
    }
}