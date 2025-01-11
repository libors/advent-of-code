package cz.libors.aoc.aoc19

import cz.libors.util.Day
import cz.libors.util.findLongs
import cz.libors.util.readToText
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.Executors

@Day("Category Six")
object Day23 {

    @JvmStatic
    fun main(args: Array<String>) {
        val input = readToText("input23.txt").findLongs().toLongArray()
        TODO("fix parallel")
        println(task1(input))
        println(task2(input))
    }

    data class Packet(val address: Int, val x: Long, val y: Long)

    private interface Handler {
        fun registerComputer(computer: NetworkIFace)
        fun receivePacket(packet: Packet)
        fun getValue(): Long
    }

    private class ReturnValueHandler(private val computers: MutableList<Computer> = ArrayList()) : Handler {
        private var value = 0L

        override fun registerComputer(computer: NetworkIFace) {
            computers.add(computer)
        }

        override fun receivePacket(packet: Packet) {
            this.value = packet.y
            computers.forEach { it.stop() }
        }

        override fun getValue() = value
    }

    private class Nat : Handler {
        private val computers = Collections.synchronizedList(ArrayList<NetworkIFace>())
        private var packet: Packet? = null
        private var lastSentPacket: Packet? = null

        init {
            Thread {
                while (true) {
                    if (computers.isNotEmpty()) {
                        val idles = computers.map { it.isIdle() }.count { it }
                        if (idles == computers.size) {
                            if (lastSentPacket == packet) {
                                computers.forEach { it.stop() }
                            }
                            computers[0].sendPacket(packet!!)
                            lastSentPacket = packet
                        }
                    }
                    Thread.sleep(3000)
                }
            }.apply { this.isDaemon = true }.start()
        }

        override fun registerComputer(computer: NetworkIFace) {
            computers.add(computer)
        }

        override fun receivePacket(packet: Packet) {
            this.packet = packet
        }

        override fun getValue(): Long {
            return lastSentPacket!!.y
        }

    }

    private fun task1(code: LongArray): Long {
        val handler = ReturnValueHandler()
        runSetup(code, handler)
        return handler.getValue()
    }

    private fun task2(code: LongArray): Long {
        val handler = Nat()
        runSetup(code, handler)
        return handler.getValue()
    }

    private fun runSetup(code: LongArray, handler: Handler) {
        val computersNumber = 50
        val queues = (0 until computersNumber).map { LinkedList<Long>() }

        val ifaces = (0 until computersNumber).map {
            NetworkIFace(it, code.copyOf(), queues) { packet -> handler.receivePacket(packet) }
        }
        ifaces.forEach { handler.registerComputer(it) }
        val executor = Executors.newFixedThreadPool(computersNumber)
        ifaces.map { iface -> executor.submit { iface.runCode() } }.forEach { it.get() }
        executor.shutdown()
    }

    private class ArrayNullableInput(val queue: Queue<Long>) : Input {
        override fun read(): Long = queue.poll() ?: -1L
    }

    private class DistributedOutput(private val queues: List<Queue<Long>>, private val returnVal: (Packet) -> Unit) :
        Output {
        companion object {
            val lock = Object()
        }

        private val buffer = ArrayList<Long>()

        override fun write(value: Long) {
            synchronized(lock) {
                buffer.add(value)
                if (buffer.size == 3) {
                    if (buffer[0] == 255L) {
                        returnVal(Packet(buffer[0].toInt(), buffer[1], buffer[2]))
                    } else {
                        val queue = queues[buffer[0].toInt()]
                        queue.add(buffer[1])
                        queue.add(buffer[2])
                    }
                    buffer.clear()
                }
            }
        }

        override fun get(): List<Long> {
            throw RuntimeException("Get output not implemented")
        }
    }

    private class NetworkIFace(addr: Int, code: LongArray, queues: List<Queue<Long>>, ret: (Packet) -> Unit) :
        ComputerImpl(
            "iface$addr",
            code,
            ArrayNullableInput(queues[addr].apply { add(addr.toLong()) }),
            DistributedOutput(queues, ret)
        ) {

        private var idleCheck = false

        override fun readInput(): Long {
            val value = super.readInput()
            if (value != -1L) idleCheck = false
            return value
        }

        fun isIdle() = idleCheck.also { idleCheck = true }

        fun sendPacket(packet: Packet) {
            val queue = (input as ArrayNullableInput).queue
            queue.add(packet.x)
            queue.add(packet.y)
        }
    }
}