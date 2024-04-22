package softmax

import chisel3._
import chisel3.util._
import chisel3.stage._
import scala.io.Source

import define.MACRO._


class SoftMax_Input extends Bundle {
    val data_in = Input(Vec(datain_bandwidth, UInt(bitwidth.W)))
}


class softmax extends Module {
    // val data_in  = IO(Flipped(Decoupled(new SoftMax_Input)))

    // val SEFP_Generator = Module(new SEFP_GeneratorUnit(bitwidth)).io

    printf("Hello, start from this\n");
}

class SEFP_GeneratorUnit(bitwidth: Int) extends Module {
    /*
       单元功能：对齐指数
       输入：输入的FP16数据（向量）
       输出：对齐后的指数、尾数，最大值
    */
    val io = IO(new Bundle {
        val en          = Input(Bool())
        val raw_data_i  = Input(Vec(datain_bandwidth, UInt(bitwidth.W)))
        val max_o       = Output(UInt(bitwidth.W))
        val sign_o      = Output(Valid(UInt(1.W)))
        val exp_o       = Output(Valid(UInt(exp_bitwidth.W)))
        val frac_o      = Output(Valid(UInt(frac_bitwidth.W)))
    })


}

class DualPortSRAM(depth: Int, width: Int = 64) extends Module {
    val io = IO(new Bundle {
        // 定义读端口
        val readAddr1 = Input(UInt(log2Ceil(depth).W))
        val readSel1 = Input(UInt(2.W))
        val readData1 = Output(UInt(16.W))

        val readAddr2 = Input(UInt(log2Ceil(depth).W))
        val readSel2 = Input(UInt(2.W))
        val readData2 = Output(UInt(16.W))

        // 定义写端口
        val writeAddr1 = Input(UInt(log2Ceil(depth).W))
        val writeData1 = Input(UInt(width.W))
        val writeEnable1 = Input(Bool())

        val writeAddr2 = Input(UInt(log2Ceil(depth).W))
        val writeData2 = Input(UInt(width.W))
        val writeEnable2 = Input(Bool())
    })

    // 实例化一块同步的内存
    val mem = SyncReadMem(depth, UInt(width.W))

    // 写入逻辑
    when(io.writeEnable1) {
        mem.write(io.writeAddr1, io.writeData1)
    }
    when(io.writeEnable2) {
        mem.write(io.writeAddr2, io.writeData2)
    }

    // 读取逻辑，根据选择信号读取对应的16位
    def readSegment(addr: UInt, sel: UInt): UInt = {
        val fullData = mem.read(addr)
        MuxCase(0.U(16.W), Array(
          (sel === 0.U) -> fullData(15, 0),
          (sel === 1.U) -> fullData(31, 16),
          (sel === 2.U) -> fullData(47, 32),
          (sel === 3.U) -> fullData(63, 48)
        ))
    }

    io.readData1 := readSegment(io.readAddr1, io.readSel1)
    io.readData2 := readSegment(io.readAddr2, io.readSel2)
}

class ExpLUT(depth: Int, width: Int = 64 , n: Int = 4) extends Module {
    val io = IO(new Bundle {
        val readAddr = Input(Vec(2 * n, UInt(log2Ceil(depth).W)))
        val readData = Output(Vec(2 * n, UInt(16.W)))
        val readSel = Input(Vec(2 * n, UInt(2.W)))

        val writeAddr = Input(Vec(2, UInt(log2Ceil(depth).W)))
        val writeData = Input(Vec(2, UInt(width.W)))
        val writeEnable1 = Input(Bool())
        val writeEnable2 = Input(Bool())
    })

    // 实例化n个双端口SRAM
    val srams = Seq.fill(n)(Module(new DualPortSRAM(depth, width)))

    // 写入逻辑，写入所有sram
    for (sram <- srams) {
        sram.io.writeEnable1 := io.writeEnable1
        sram.io.writeAddr1 := io.writeAddr(0)
        sram.io.writeData1 := io.writeData(0)

        sram.io.writeEnable2 := io.writeEnable2
        sram.io.writeAddr2 := io.writeAddr(1)
        sram.io.writeData2 := io.writeData(1)
    }

    // 读取逻辑，从每个sram的两个端口读取数据，并通过寄存器输出
    val regReadData = Reg(Vec(2 * n, UInt(16.W)))  // 定义寄存器数组来存储输出数据
    for (i <- 0 until 2 * n by 2) {
        val sramIndex = i / 2
        val sram = srams(sramIndex)
        sram.io.readAddr1 := io.readAddr(i)
        sram.io.readSel1 := io.readSel(i)
        regReadData(i) := sram.io.readData1

        sram.io.readAddr2 := io.readAddr(i + 1)
        sram.io.readSel2 := io.readSel(i + 1)
        regReadData(i + 1) := sram.io.readData2
    }

    io.readData := regReadData
}


