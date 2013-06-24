package ie.tcd.cs.nembes.coror.util;


public class NumberUtil {
	
	/**
	 * Not a number type
	 */
	public static byte TYPE_NONNUMBER = 0x00;
	
	/**
	 * The Integer type.
	 */
	public static byte TYPE_INTEGER = 0x01;

	/**
	 * The Byte type.
	 */
	public static byte TYPE_BYTE = 0x02;
	
	/**
	 * The Double type.
	 */
	public static byte TYPE_DOUBLE = 0x03;
	
	/**
	 * The Float type.
	 */
	public static byte TYPE_FLOAT = 0x04;
	
	/**
	 * The Long type.
	 */
	public static byte TYPE_LONG = 0x05;
	
	/**
	 * The Short type.
	 */
	public static byte TYPE_SHORT = 0x06;
	
	
	
	public static boolean isNumber(Object n){
		if(n instanceof Integer){
			return true;
		}
		else if(n instanceof Byte){
			return true;
		}
		else if(n instanceof Double){
			return true;
		}
		else if(n instanceof Float){
			return true;
		}
		else if(n instanceof Long){
			return true;
		}
		else if(n instanceof Short){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static byte getNumberType(Object n){
		if(n instanceof Integer){
			return TYPE_INTEGER;
		}
		else if(n instanceof Byte){
			return TYPE_BYTE;
		}
		else if(n instanceof Double){
			return TYPE_DOUBLE;
		}
		else if(n instanceof Float){
			return TYPE_FLOAT;
		}
		else if(n instanceof Long){
			return TYPE_LONG;
		}
		else if(n instanceof Short){
			return TYPE_SHORT;
		}
		else{
			return TYPE_NONNUMBER;
		}		
	}
	
	public static Long asLong(Object n){
		if(n instanceof Long)
			return (Long)n;
		else if(n instanceof Integer){
			return new Long(((Integer)n).longValue());
		}
		else if(n instanceof Byte){
			return new Long(((Byte)n).byteValue());
		}
		else if(n instanceof Double){
			return new Long(((Double)n).longValue());
		}
		else if(n instanceof Float){
			return new Long(((Float)n).longValue());
		}
		else if(n instanceof Short){
			return new Long(((Short)n).shortValue());
		}
		else {
			return null;
		}
	}
	
	public static Double asDouble(Object n){
		if(n instanceof Double)
			return (Double)n;
		else if(n instanceof Float)
			return new Double(((Float)n).doubleValue());
		else if(n instanceof Integer)
			return new Double(((Integer)n).doubleValue());
		else if(n instanceof Long)
			return new Double(((Long)n).doubleValue());
		else if(n instanceof Short)
			return new Double(((Short)n).shortValue());
		else if(n instanceof Byte){
			return new Double(((Byte)n).byteValue());
		}
		else
			return null;
	}
	
	public static Integer asInteger(Object n){
		if(n instanceof Integer)
			return (Integer)n;

		else if(n instanceof Byte){
			return new Integer(((Byte)n).byteValue());
		}
		else if(n instanceof Double){
			return new Integer(((Double)n).intValue());
		}
		else if(n instanceof Float){
			return new Integer(((Float)n).intValue());
		}
		else if(n instanceof Short){
			return new Integer(((Short)n).shortValue());
		}
		else {
			return null;
		}		
	}

}

