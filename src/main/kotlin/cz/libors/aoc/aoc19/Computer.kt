package cz.libors.aoc.aoc19

import cz.libors.util.nthDigit
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

private const val debugComputerOn = false

fun debugComp(message: String) {
    if (debugComputerOn) {
        println(message)
    }
}

interface Computer {

    companion object {
        fun create(
            code: LongArray, input: Input = Input.value(), output: Output = ArrayOutput(),
            memory: ComputerImpl.Memory = ComputerImpl.MapMemory(code)
        ): Computer =
            ComputerImpl("computer", code.clone(), input, output, memory)

        fun create(
            name: String,
            code: LongArray,
            input: Input = Input.value(),
            output: Output = ArrayOutput()
        ): Computer =
            ComputerImpl(name, code.clone(), input, output)
    }

    fun stop()
    fun runCode(): Computer
    fun getOutput(): List<Long>
    fun getLastOutput(): Long
    fun getMemoryState(): Map<Long, Long>
}

interface Ctx {
    fun get(argPos: Int): Long
    fun put(argPos: Int, value: Long)
    fun writeOutput(value: Long)
    fun readInput(): Long
    fun updatePos(pos: Long)
    fun updateRelativeBase(value: Long)
    fun stop()
}

class Instruction(val name: String, val code: Int, val operands: Int, val op: (Ctx) -> Unit)

interface Input {
    companion object {
        fun value(vararg v: Long): Input = ArrayInput(LinkedList(v.toList()))
        fun queue(q: BlockingQueue<Long>): Input = QueueInput(q)
    }

    fun read(): Long
}

private class ArrayInput(val queue: Queue<Long>) : Input {
    override fun read(): Long = queue.poll() ?: throw RuntimeException("Input is empty")
}

open class QueueInput(val queue: BlockingQueue<Long>) : Input {
    override fun read(): Long = queue.poll(3, TimeUnit.SECONDS) ?: throw RuntimeException("Input is empty")
}

interface Output {
    companion object {
        fun queue(q: BlockingQueue<Long>): Output = QueueOutput(q)
    }

    fun write(value: Long)
    fun get(): List<Long>
}

open class ArrayOutput : Output {
    private val array = LinkedList<Long>()
    override fun write(value: Long) {
        array.add(value)
    }

    override fun get(): List<Long> = array.toList()
}

private class QueueOutput(val queue: BlockingQueue<Long>) : Output {
    override fun write(value: Long) {
        queue.put(value)
    }

    override fun get(): List<Long> = throw RuntimeException("Cannot get result from queue")
}

open class ComputerImpl(
    private val name: String,
    codeInput: LongArray,
    val input: Input,
    private val output: Output,
    private val memory: Memory = MapMemory(codeInput)
) : Computer, Ctx {

    private val instr: Array<Instruction>
    private lateinit var opCode: OpCode
    private var pos = 0L
    private var relBase = 0L
    private var stop = false

    init {
        val undefined = Instruction("undefined", 0, 0) { throw RuntimeException("undefined instruction") }
        instr = Array(100) { undefined }
        instructions.forEach { instr[it.code] = it }
    }

    override fun runCode(): Computer {
        do {
            opCode = OpCode(mem(pos))
            val instruction = instr[opCode.code]
            if (debugComputerOn) cdebug("#${instruction.name} ($opCode)")
            val origPos = pos
            instruction.op.invoke(this)
            if (origPos == pos) {
                pos += instruction.operands + 1
            }
        } while (!stop)
        return this
    }

    override fun stop() {
        stop = true
    }

    private fun mem(value: Long): Long = memory[value]

    override fun get(argPos: Int): Long = mem(address(argPos))
        .also { if (debugComputerOn) cdebug("get from ${address(argPos)}") }

    override fun put(argPos: Int, value: Long) {
        val targetAddress = address(argPos)
        if (debugComputerOn) cdebug("save $value to [$targetAddress]")
        memory[targetAddress] = value
    }

    private fun address(argPos: Int): Long = when (opCode.modes[argPos - 1]) {
        Address.positional -> mem(pos + argPos)
        Address.immediate -> pos + argPos
        Address.relative -> mem(pos + argPos) + relBase
        else -> throw RuntimeException("Unknown opcode mode: ${opCode.modes[argPos - 1]}")
    }

    override fun writeOutput(value: Long) {
        if (debugComputerOn) cdebug("output $value")
        output.write(value)
    }

    override fun readInput(): Long {
        val value = input.read()
        if (debugComputerOn) cdebug("reading input: $value")
        return value
    }

    override fun updatePos(pos: Long) {
        if (debugComputerOn) if (stop) cdebug("exit") else cdebug("jump to $pos")
        this.pos = pos
    }

    override fun updateRelativeBase(value: Long) {
        relBase += value
    }

    override fun getOutput(): List<Long> = output.get()

    override fun getLastOutput(): Long = output.get().last()

    override fun getMemoryState(): Map<Long, Long> = memory.toMap().toSortedMap()

    protected fun cdebug(message: String) {
        debugComp("($name @$pos) $message")
    }

    interface Memory {
        operator fun get(address: Long): Long
        operator fun set(address: Long, value: Long)
        fun toMap(): Map<Long, Long>
    }

    class MapMemory(code: LongArray) : Memory {
        private val map = code.mapIndexed { idx, value -> Pair(idx.toLong(), value) }.toMap().toMutableMap()

        override fun get(address: Long): Long {
            return map[address] ?: 0
        }

        override fun set(address: Long, value: Long) {
            map[address] = value
        }

        override fun toMap(): Map<Long, Long> {
            return map
        }

    }

    class ArrayMemory(code: LongArray) : Memory { // does not support arbitrary memory size, but is faster
        private val array = LongArray(code.size * 2)

        init {
            code.copyInto(array)
        }

        override operator fun get(address: Long): Long {
            require(address < array.size)
            return array[address.toInt()]
        }

        override operator fun set(address: Long, value: Long) {
            require(address < array.size)
            array[address.toInt()] = value
        }

        override fun toMap(): Map<Long, Long> {
            return array.mapIndexed { idx, value -> Pair(idx.toLong(), value) }.toMap()
        }
    }

    object Address {
        const val positional = 0
        const val immediate = 1
        const val relative = 2
    }

    private class OpCode(val code: Int, val modes: IntArray) {
        constructor(opCode: Long) :
                this((opCode % 100).toInt(), intArrayOf(opCode.nthDigit(3), opCode.nthDigit(4), opCode.nthDigit(5)))

        override fun toString() = "" + code + ":" + modes.toList()
    }
}

private val instructions = listOf(
    Instruction("add", 1, 3) { it.put(3, it.get(1) + it.get(2)) },
    Instruction("multiply", 2, 3) { it.put(3, it.get(1) * it.get(2)) },
    Instruction("input", 3, 1) { it.put(1, it.readInput()) },
    Instruction("output", 4, 1) { it.writeOutput(it.get(1)) },
    Instruction("jumpIfTrue", 5, 2) { if (it.get(1) > 0) it.updatePos(it.get(2)) },
    Instruction("jumpIfFalse", 6, 2) { if (it.get(1) == 0L) it.updatePos(it.get(2)) },
    Instruction("lessThan", 7, 3) {
        if (it.get(1) < it.get(2))
            it.put(3, 1) else it.put(3, 0)
    },
    Instruction("equals", 8, 3) {
        if (it.get(1) == it.get(2))
            it.put(3, 1) else it.put(3, 0)
    },
    Instruction("relBase", 9, 1) { it.updateRelativeBase(it.get(1)) },
    Instruction("exit", 99, 0) { it.stop() }
)
