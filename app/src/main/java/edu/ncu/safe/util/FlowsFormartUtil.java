package edu.ncu.safe.util;

public class FlowsFormartUtil {
	public static String toFlowsFormart(long flowsByte){
		float flowsK = ((float)flowsByte)/1024;
		if(flowsK<=1){
			return flowsByte+"B";
		}
		float flowsM = flowsK/1024;
		if(flowsM<=1){
			return String.format("%.2fK", flowsK);
		}
		
		float flowsG = flowsM/1024;
		if(flowsG<=1){
			return  String.format("%.2fM", flowsM);
		}
		
		float flowsT = flowsG/1024;
		if(flowsT<=1){
			return  String.format("%.2fG", flowsG);
		}else{
			return  String.format("%.2fT", flowsT);
		}
	}
	
	public static String toMBFormat(long data){
		float flowsM = ((float) data)/(1024*1024);
		String re = String.format("%.2f", flowsM);
		if(re.endsWith(".00")||re.length()>5){
			return (int)flowsM +"";
		}
		if(re.endsWith("0")){
			re=re.substring(0,re.length()-1);
		}
		return  re;
	}
	
	public static String toFlowsSpeedFormart(long flowsByte){
		float flowsK = ((float)flowsByte)/1024;
		if(flowsK<=1){
			return to11LengthString(flowsByte+"B/s");
		}
		float flowsM = flowsK/1024;
		if(flowsM<=1){
			return to11LengthString(String.format("%.2fK/s", flowsK));
		}
		
		float flowsG = flowsM/1024;
		if(flowsG<=1){
			return to11LengthString(String.format("%.2fM/s", flowsM));
		}
		
		float flowsT = flowsG/1024;
		if(flowsT<=1){
			return to11LengthString(String.format("%.2fG/s", flowsG));
		}else{
			return to11LengthString(String.format("%.2fT/s", flowsT));
		}
	}
	
	private static String to11LengthString(String res){
		return res;
	}
}
