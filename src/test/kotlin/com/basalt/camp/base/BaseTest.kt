package com.basalt.camp.base

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import org.slf4j.LoggerFactory

open class BaseTest {
    companion object {
        private val log = LoggerFactory.getLogger(BaseTest::class.java)
    }

    @Rule
    @JvmField
    val testName = TestName()

    @Before
    fun before() {
        log.info("-------------({})-------------", testName.methodName)
    }

    @After
    fun after() {
        log.info("------------------------------")
    }
}