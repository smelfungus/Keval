package com.notkamui.keval

import com.notkamui.keval.framework.Resources
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

fun hypotenuse(x: Double, y: Double): Double = sqrt(x.pow(2) + y.pow(2))

class DSLResourcesTests {
    @Test
    fun builtInOperators() {
        val operators = Resources.defaultOperators
        assertEquals("+-*/%^()".toSet(), operators.keys)
    }

    @Test
    fun testGetAllResources() {
        val operators = Resources.defaultOperators + (';' to BinaryOperator(::hypotenuse, 3, false))
        assertEquals("+-*/%^();".toSet(), operators.keys)
    }
}

class DLSTest {
    @Test
    fun checkSimpleDLS() {
        val kvl = Keval {
            +operator {
                symbol = ';'
                implementation = ::hypotenuse
                precedence = 3
            }
        }

        assertEquals(
            5.0,
            kvl.eval("3;4")
        )

        assertEquals(
            10.0,
            kvl.eval("((3;4)*1.2);8")
        )

        assertFailsWith(KevalInvalidOperatorException::class) {
            kvl.eval("6+4")
        }

        assertFailsWith(KevalInvalidOperatorException::class) {
            kvl.eval("6;(4-1)")
        }
    }

    @Test
    fun checkCombinedDSL() {
        val kvl = Keval {
            +operator {
                symbol = ';'
                implementation = ::hypotenuse
                precedence = 3
            }
            +defaultOperators
        }

        assertEquals(
            4.1,
            kvl.eval("3;4-0.9")
        )

        assertEquals(
            8.85663593,
            (kvl.eval("((3;4)-1.2);8") * 10.0.pow(8)).toInt() / 10.0.pow(8)
        )
    }
}
