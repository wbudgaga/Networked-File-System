package cs455.nfs.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
// this class contains static methods (helps methods) that are used by packing and unpacking between primitive types and byte stream
public class ByteStream {
	
		public static final byte[] getBytes(byte[]  byteStream,int start, int length){
			byte[] bytes = new byte[length];
			int end = start+length;
			for(int i = start; i< end; ++i){
				bytes[i-start] = byteStream[i];
			}
			return bytes;
		}
		
		public static final byte[] readFileBytes(FileInputStream fileInputStream, int length) throws IOException {
			if (length==0)
				return new byte[0];
	        byte [] buffer = new byte[length];
		    while (fileInputStream.read(buffer,0, length) != -1);
	        return buffer;
		}

		
		public static final byte[] intToByteArray(int value) {
	        return new byte[] {
	                (byte)(value >>> 24),// right shift 24 bits. the remaining, the most significant 8 bits are stored in position 0 in the array
	                (byte)(value >>> 16),// right shift 16 bits. the remaining, the most significant 16 bits are casting to byte. the least significant 8 bits of them are stored in position 1 in the array
	                (byte)(value >>> 8),// right shift 8 bits. the remaining, the most significant 16 bits are casting to byte. the least significant 8 bits of them are stored in position 2 in the array
	                (byte)value};	// cast value to byte,  the least significant 8 bits are stored in position 3 in the array
		}


		public static final int byteArrayToInt(byte [] b) {
	        return (b[0] << 24) // left shift 24 bits to put the contents of b[0] in int's positions from 24 to 31 
	                + ((b[1] & 0xFF) << 16) // left shift 16 bits to put the contents of b[1] in int's positions from 16 to 23
	                + ((b[2] & 0xFF) << 8) // left shift 8 bits to put the contents of b[2] in int's positions from 8 to 15
	                + (b[3] & 0xFF);	// put the contents of b[3] in int's positions from 0 to 7
		}								// casting to int will convert from binary representation to decimal
		public static final byte[] StringToByteArray(String value) {
			return value.getBytes();
		}
		
		public static final String  byteArrayToString(byte[] value) {
			return new String(value);
		}

		public static final byte[] join(byte[] array1, byte[] array2) {
			if (array1 == null)	
				return array2;
			if (array2 == null)	
				return array1;
			byte [] resultArray = new byte[array1.length + array2.length];
			System.arraycopy(array1,0,resultArray,0         ,array1.length);
			System.arraycopy(array2, 0, resultArray, array1.length, array2.length);
			return resultArray;
		}
		
		public static final byte[] packString(String value) {
			byte[] bytes = StringToByteArray(value);
			byte[] bytesLength = intToByteArray(bytes.length);
			return join(bytesLength, bytes);
		}

		public static byte[] addPacketHeader(byte [] packetBody){
			int packetLegth = packetBody.length;
			byte[] packetLegthInBytes = intToByteArray(packetLegth);
			return join(packetLegthInBytes, packetBody);
		}
	
		// remove the header which is 4 bytes at the beginning of the packet
		public static byte[] removePacketHeader(byte[] byteStream){
			return Arrays.copyOfRange(byteStream, 4, byteStream.length); 
		}

}
