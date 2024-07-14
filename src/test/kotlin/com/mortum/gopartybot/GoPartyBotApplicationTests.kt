package com.mortum.gopartybot

import com.mortum.gopartybot.utils.chooseOptimalNumber
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals


class GoPartyBotApplicationTests {

    @Test
    fun worstTestEver() {
        for (i in 0..1000000) {
            val numbers = mutableSetOf<Int>()
            val size = (2..25).random()
            while (numbers.size != size) {
                numbers.add((0..100).random())
            }
            val numbersCopy = numbers.toMutableList()
            numbersCopy.sort()
            val exp = longWay(numbersCopy)
            val act = chooseOptimalNumber(numbersCopy)

//            println(numbersCopy)
//            numbersCopy.remove(act)
//            println("мой алгос: $act")
            val sa = calc(act, numbersCopy)
//            println(sa)
//
//            numbersCopy.add(act)
//            numbersCopy.sort()
//            numbersCopy.remove(exp)
//
//            println("длинный алгос: $exp")
            val se = calc(exp, numbersCopy)
//            println(se)
            assertEquals(se, sa)
        }

    }

    private fun longWay(set: MutableList<Int>): Int {

        var min: Int = Int.MAX_VALUE
        var minE = 0
        for (i in 0..<set.size) {
            var sum = 0
            for (j in 0..<set.size) {
                if (i == j) continue
                sum += abs(set[i] - set[j])
            }
            if (sum < min) {
                min = sum
                minE = set[i]
            }
        }

        return minE
    }

    private fun calc(value: Int, list: MutableList<Int>): Int {
        var sum = 0
        for (e in list) {
            sum += abs(e - value)
        }
        return sum
    }
}
