package com.example.wsamad6.core

import org.junit.Assert.*
import org.junit.Test

class ValidationsTest {
    @Test
    fun `emailIsEmptyAndReturnFalse`() {
        val result = Validations.validateEmail("")
        assertEquals(false, result)
    }

    @Test
    fun `emailIsNullAndReturnFalse`() {
        val result = Validations.validateEmail(null)
        assertEquals(false, result)
    }

    @Test
    fun `emailIsWrongWithRegexAndReturnFalse`() {
        val result = Validations.validateEmail("healthy@gmail.com")
        assertEquals(false, result)
    }

    @Test
    fun `emailCorrectAndReturnTrue`() {
        val result = Validations.validateEmail("healthy@wsa.com")
        assertEquals(true, result)
    }

    @Test
    fun `passwordIsNullAndReturnFalse`(){
        val result = Validations.validatePassword(null)
        assertEquals(false,result)
    }
    @Test
    fun `passwordIsEmptyAndReturnFalse`(){
        val results = Validations.validatePassword("")
        assertEquals(false,results)
    }
    @Test
    fun `passwordIsLongAndWrongWithRegexAndReturnFalse`(){
        val result = Validations.validatePassword("123456")
        assertEquals(false,result)
    }
    @Test
    fun `passwordIsHaveALetterWrongWithRegexAndReturnFalse`(){
        val result = Validations.validatePassword("a1234")
        assertEquals(false,result)
    }
    @Test
    fun `passwordIsCorrectAndReturnTrue`(){
        val result = Validations.validatePassword("1234")
        assertEquals(true,result)
    }
}