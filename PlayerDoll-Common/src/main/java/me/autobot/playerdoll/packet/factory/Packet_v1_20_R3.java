package me.autobot.playerdoll.packet.factory;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.packet.PacketFactory;
import me.autobot.playerdoll.packet.Packets;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class Packet_v1_20_R3 extends PacketFactory {
    public Packet_v1_20_R3(SocketReader socketReader) {
        super(socketReader);
    }

    @Override
    protected int getProtocol() {
        return 765;
    }

    @Override
    protected int getConfigAckId() {
        return 0x02;
    }

    @Override
    protected int getKeepAlivePacketId(SocketReader.ConnectionState state) {
        return state == SocketReader.ConnectionState.PLAY ? 0x15 : 0x03;
    }

    @Override
    public void processLogin(int packetID, DataInputStream data, int dataLength) throws IOException {
        switch (packetID) {
            // Disconnect Packet
            case 0x00 -> socketReader.endStream();
            // Login Success (Profile)
            case 0x02 -> {
                output.write(loginAck());
                socketReader.nextState();
            }
            // Setup Compression
            case 0x03 -> socketReader.setCompressionThreshold(Packets.readVarInt(data));
            // Encryption Packet, Plugin Message
            //case 0x01, 0x04 -> {}
            //default -> System.out.println("Unknown Login Packet ID: " + packetID);
        }
    }

    @Override
    public void processConfiguration(int packetID, DataInputStream data, int dataLength) throws IOException {
        switch (packetID) {
            // Disconnect
            case 0x01 -> {
                PlayerDoll.LOGGER.info("Client Disconnect (config phase)");
                socketReader.endStream();
            }
            // Finish Configuration
            case 0x02 -> {
                //System.out.println("Finish Config");
                output.write(configAck());
                socketReader.nextState();
            }
            // Keep Alive
            case 0x03 -> output.write(keepAlive(socketReader.getCurrentState(), data.readLong()));
            // Plugin Message 0x00
            // Pong 0x04
            // Registry 0x05
            // Remove Resource Pack (resourcePack Pop) Config 0x06
            // Add Resource Pack (resource Pack Push) Config 0x07
            // Feature Flags 0x08
            // Tags 0x09
            //case 0x00, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09 -> {}
            //default -> System.out.println("Unknown Config Packet ID: " + packetID);
        }
    }

    @Override
    public void processPlay(int packetID, DataInputStream data, int dataLength) throws IOException {
        switch (packetID) {
            // Disconnect
            case 0x1B -> {
                PlayerDoll.LOGGER.info("Client Disconnected (play phase)");
                socketReader.endStream();
            }
            // Keep Alive
            case 0x24 -> output.write(keepAlive(socketReader.getCurrentState(), data.readLong()));
            case 0x44 -> {
                // Weird problem when sending resource pack respond
                //System.out.println("ResourcePack Push (Play)");
                UUID uuid = Packets.readUUID(data);
                String url = Packets.readString(data);
                String hash = Packets.readString(data);
                boolean forced = data.readBoolean();
                if (forced) {
                    //System.out.println("It is a forced Resource Pack");
                    //System.out.println("Not implemented, not respond to this");
                } else {
                    //System.out.println("It is an optional Resource Pack, Skip");
                    resourcePackPush(socketReader.getCurrentState(), uuid, ResourcePackStatus.DECLINED);
                }
            }
            //default -> System.out.println("Unknown Play Packet ID: " + packetID);
        }
    }
}