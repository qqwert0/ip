// package ip
package project_foo
import common.Math
import chisel3._
import chisel3.util._
import common.axi._
import common.storage._
import common._


class ip_handler (
    myip    :   UInt
) extends Module{
    val io = IO(new Bundle{
        val data_in          =   Flipped(Decoupled(new AXIS(512)))
        val arp_out          =   Decoupled(new AXIS(512))
        val tcp_out          =   Decoupled(new AXIS(512))
        val udp_out          =   Decoupled(new AXIS(512))
        val icmp_out         =   Decoupled(new AXIS(512))
        val roce_out         =   Decoupled(new AXIS(512))
	})
    


    val count               = RegInit(UInt(2.W), 0.U)
    
    val detect_eth_protocol1 = Module(new detect_eth_protocol)
    val route_by_eth1       = Module(new route_by_eth)
    val extract_ip_meta1    = Module(new extract_ip_meta)
    val compute_checksum1   = Module(new compute_checksum)
    val ip_invalid_dropper1 = Module(new ip_invalid_dropper)
    val cut_length1         = Module(new cut_length)
    val detect_ipv4_protocol1 = Module(new detect_ipv4_protocol)
    val rshift              = Module(new RSHIFT(14,512))
    val shift_last          = RegInit(UInt(1.W), 1.U)
    io.data_in.ready                     := detect_eth_protocol1.io.data_in.ready 
    detect_eth_protocol1.io.data_in.bits := io.data_in.bits
    detect_eth_protocol1.io.data_in.valid:= io.data_in.valid
    detect_eth_protocol1.io.data_out.ready:= route_by_eth1.io.data_in.ready
    route_by_eth1.io.ipv4_out.ready      := rshift.io.in.ready
    route_by_eth1.io.data_in.bits        := detect_eth_protocol1.io.data_out.bits
    route_by_eth1.io.data_in.valid       := detect_eth_protocol1.io.data_out.valid
    route_by_eth1.io.etherType           := detect_eth_protocol1.io.eth_protocol
    route_by_eth1.io.ARP_out.ready       := io.arp_out.ready
    io.arp_out.bits                      := route_by_eth1.io.ARP_out.bits
    io.arp_out.valid                     := route_by_eth1.io.ARP_out.valid
    rshift.io.in.valid                   := route_by_eth1.io.ipv4_out.valid
    rshift.io.in.bits.data               := route_by_eth1.io.ipv4_out.bits.data
    rshift.io.in.bits.last               := route_by_eth1.io.ipv4_out.bits.last
    rshift.io.in.bits.keep               := route_by_eth1.io.ipv4_out.bits.keep
    rshift.io.out.ready                  := extract_ip_meta1.io.data_in.ready
    extract_ip_meta1.io.myip             := myip
    extract_ip_meta1.io.data_in.valid    := rshift.io.out.valid
    extract_ip_meta1.io.data_in.bits     := rshift.io.out.bits
    extract_ip_meta1.io.data_out.ready   := compute_checksum1.io.data_in.ready
    compute_checksum1.io.data_in.bits    := extract_ip_meta1.io.data_out.bits
    compute_checksum1.io.data_in.valid   := extract_ip_meta1.io.data_out.valid
    compute_checksum1.io.data_out.ready  := ip_invalid_dropper1.io.data_in.ready
    ip_invalid_dropper1.io.data_in.valid := compute_checksum1.io.data_out.valid
    ip_invalid_dropper1.io.data_in.bits  := compute_checksum1.io.data_out.bits
    ip_invalid_dropper1.io.validchecksum := compute_checksum1.io.checksumvalid
    ip_invalid_dropper1.io.validipaddress:= extract_ip_meta1.io.validipaddr
    ip_invalid_dropper1.io.data_out.ready:= cut_length1.io.data_in.ready
    cut_length1.io.data_in.bits          := ip_invalid_dropper1.io.data_out.bits
    cut_length1.io.data_in.valid         := ip_invalid_dropper1.io.data_out.valid
    cut_length1.io.data_out.ready        := detect_ipv4_protocol1.io.data_in.ready 
    detect_ipv4_protocol1.io.data_in.bits:= cut_length1.io.data_out.bits
    detect_ipv4_protocol1.io.data_in.valid:= cut_length1.io.data_out.valid
    detect_ipv4_protocol1.io.type1        := extract_ip_meta1.io.ipv4Type
    detect_ipv4_protocol1.io.tcp_out.ready:= io.tcp_out.ready
    detect_ipv4_protocol1.io.udp_out.ready:= io.udp_out.ready
    detect_ipv4_protocol1.io.icmp_out.ready:= io.icmp_out.ready
    detect_ipv4_protocol1.io.roce_out.ready:= io.roce_out.ready
    io.tcp_out.valid                     := detect_ipv4_protocol1.io.tcp_out.valid
    io.tcp_out.bits                      := detect_ipv4_protocol1.io.tcp_out.bits
    io.udp_out.valid                     := detect_ipv4_protocol1.io.udp_out.valid
    io.udp_out.bits                      := detect_ipv4_protocol1.io.udp_out.bits
    io.icmp_out.valid                     := detect_ipv4_protocol1.io.icmp_out.valid
    io.icmp_out.bits                      := detect_ipv4_protocol1.io.icmp_out.bits
    io.roce_out.valid                     := detect_ipv4_protocol1.io.roce_out.valid
    io.roce_out.bits                      := detect_ipv4_protocol1.io.roce_out.bits

    // val tcp_out          =   Decoupled(new AXIS(512))
    //     val udp_out          =   Decoupled(new AXIS(512))
    //     val icmp_out         =   Decoupled(new AXIS(512))
    //     val roce_out         =   Decoupled(new AXIS(512))

    // io.arp_out          := 
}