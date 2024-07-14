package com.mortum.gopartybot.utils

fun chooseOptimalNumber(mutableList: MutableList<Int>): Int {
    if (mutableList.size == 1) return mutableList[0]

    mutableList.sort()

    return mutableList[mutableList.size / 2]
}