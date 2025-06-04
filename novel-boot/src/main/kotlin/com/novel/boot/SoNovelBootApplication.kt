package com.novel.boot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SoNovelBootApplication

fun main(args: Array<String>) {
    runApplication<SoNovelBootApplication>(*args)
}
