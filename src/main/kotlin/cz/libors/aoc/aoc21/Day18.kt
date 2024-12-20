package cz.libors.aoc.aoc21

import cz.libors.util.Day
import cz.libors.util.TreeNode
import cz.libors.util.readTree

@Day("Snailfish")
object Day18 {

    @JvmStatic
    fun main(args: Array<String>) {
        //val input = readToLines("test.txt")
        val input = "[[[[1,3],[5,3]],[[1,3],[8,7]]],[[[4,9],[6,9]],[[8,2],[7,3]]]]"
            .readTree { it.toInt() }
        TODO("not implemented yet")
    }

    private fun task1() {

    }

    private class SPair(var left: SPair?, var right: SPair?, var num: Int?, var parent: SPair?) {
        constructor(num: Int, parent: SPair?): this(null, null, num, parent)
        constructor(left: SPair, right: SPair, parent: SPair?): this(left, right, null, parent)

        fun isNum() = num != null
        fun plus(y: SPair): SPair {
            val result = SPair(this, y, null)
            result.left!!.parent = result
            result.right!!.parent = result
            return result
        }
        fun split() {
            left = SPair(num!! / 2, this)
            right = SPair(num!! / 2 + num !! % 2, this)
            num = null
        }
//        fun needsExplode(): SPair?  {
//            fun find(x: SPair, n: Int): SPair? {
//                if (isNum()) {
//
//                }
//            }
//
//        }
        fun needsSplit(): SPair? {
            if (isNum()) {
                return if (num!! < 10) null else this
            } else {
                val l = left!!.needsSplit()
                if (l != null) return l
                val r = right!!.needsSplit()
                if (r != null) return r
                return null
            }
        }
        fun topParent(): SPair = if (parent == null) this else parent!!.topParent()
        fun order(): List<SPair> {
            val result = mutableListOf<SPair>()
            fun collect(n: SPair, acc: MutableList<SPair>) {
                if (n.isNum())
                    acc.add(this)
                else {
                    collect(n.left!!, acc)
                    collect(n.right!!, acc)
                }
            }
            return result
        }

        fun magnitude(): Int = if (isNum()) num!! else left!!.magnitude() * 3 + right!!.magnitude() * 2
        fun explode() {
            val l = left!!.num!!
            val r = right!!.num!!
            parent!!.left = null
            parent!!.right = null
            parent!!.num = 0
            val list = this.topParent().order()
            val idx = list.indexOf(parent)
            if (idx > 0) {
                val le = list[idx - 1]
                le.num = le.num!! + l
            }
            if (idx < list.size - 1) {
                val ri = list[idx + 1]
                ri.num = ri.num!! + r
            }
        }
    }

    private fun toSnail(x: TreeNode<Int>): SPair =
        if (x.isValue())
            SPair(x.v!!, null)
        else {
            val left = toSnail(x.items[0])
            val right = toSnail(x.items[1])
            val result = SPair(left, right, null)
            left.parent = result
            right.parent = result
            result
        }

}