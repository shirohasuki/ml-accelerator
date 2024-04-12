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

class ExpLookup(index_width:Int,mantissa_width:Int,exp_result_width:Int)extends Module {
  val io = IO(new Bundle {
    val index = Input(UInt(index_width.W))    // 5位指数输入
    val mantissa = Input(UInt(mantissa_width.W)) // 10位尾数输入
    val result = Output(UInt(exp_result_width.W))  // 16位浮点数输出
  })

  // 读取文件并解析十六进制字符串
  val filename = "/home/zzp/zzp/ml-accelerator/tool/fp16_exp_lut.chisel"
  val fileLines = Source.fromFile(filename).getLines().toArray
  val expTable = fileLines.map(line => Integer.parseInt(line.trim, 16).U(exp_result_width.W))

  // 将解析后的数组转换为 Vec
  val expTableVec = VecInit(expTable)

  // 计算查表索引
  val tableIndex = io.index * (1.U << mantissa_width) + io.mantissa // index * 2^mantissa_width + mantissa

  // 查表操作，输出对应的 fp16 结果
  io.result := expTableVec(tableIndex)
}
