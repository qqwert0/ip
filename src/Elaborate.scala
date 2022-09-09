package project_foo
import common._
import cmac._
import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}
import firrtl.options.TargetDirAnnotation
import qdma._


object elaborate extends App {
	println("Generating a %s class".format(args(0)))
	val stage	= new chisel3.stage.ChiselStage
	val arr		= Array("-X", "sverilog", "--full-stacktrace")
	val dir 	= TargetDirAnnotation("Verilog")

	args(0) match{
		case "QDMATop" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new QDMATop()),dir))
		case "H2CLatencyTop" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new QDMALatencyTop()),dir))
		case "H2CLatency" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new H2CLatency()),dir))
		// case "simpleTop" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new simpleTop()),dir))
		// case "simpleTest" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new simpleTest()),dir))
		case "Foo" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new Foo()),dir))
		case "ramTest2" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new ramTest2()),dir))
		case "queueTest" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new queueTest()),dir))
		case "arp" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new arp("hE59D02350A00".U,0X11112222.U)),dir))
		// case "axitest" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new axitest(0X15.U,0X56.U)),dir))
		case "mac_ip_encode" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new mac_ip_encode(0x15.U,0x56.U)),dir))
		case "compute_checksum1" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new compute_checksum1()),dir))
		case "IPTest" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new IPTest()),dir))
		case "IPTopSend" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new IPTopSend()),dir))
		case "IPTopRec" => stage.execute(arr,Seq(ChiselGeneratorAnnotation(() => new IPTopRec()),dir))
		case _ => println("Module match failed!")
	}
}