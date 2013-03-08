package com.sneezer.parkrio;

import java.util.HashMap;
import java.util.Map;

public class Measurement {
	public static String[] kindList= new String[] {"elec","hotwater","heat","water","gas"};
	
	private float elec = 0;	// 전기
	private float hotwater = 0;	// 온수
	private float heat = 0;	// 난방
	private float water = 0;	// 수도
	private float gas = 0;	// 가스
	
	public float getElec() {
		return elec;
	}
	public void setElec(float elec) {
		this.elec = elec;
	}
	public float getHotwater() {
		return hotwater;
	}
	public void setHotwater(float hotwater) {
		this.hotwater = hotwater;
	}
	public float getHeat() {
		return heat;
	}
	public void setHeat(float heat) {
		this.heat = heat;
	}
	public float getWater() {
		return water;
	}
	public void setWater(float water) {
		this.water = water;
	}
	public float getGas() {
		return gas;
	}
	public void setGas(float gas) {
		this.gas = gas;
	}
	public String toString() {
		return (new String(this.elec+";"+this.hotwater+";"+this.heat+";"+this.water+";"+this.gas));
	}
	public Map<String, Float> getObject(){
		Map<String, Float> resultMap = new HashMap<String,Float>();
		resultMap.put("Elect", elec);
		resultMap.put("Hotwater", hotwater);
		resultMap.put("Heat", heat);
		resultMap.put("Water", water);
		resultMap.put("Gas", gas);
		
		return resultMap;
	}
	
	public Measurement compare(Measurement smallMeasure) {
		Measurement resultMeasure = new Measurement();
		
		resultMeasure.setElec(elec - smallMeasure.getElec());
		resultMeasure.setHotwater(hotwater - smallMeasure.getHotwater());
		resultMeasure.setHeat(heat - smallMeasure.getHeat());
		resultMeasure.setWater(water - smallMeasure.getWater());
		resultMeasure.setGas(gas - smallMeasure.getGas());
		
		return resultMeasure;
	}
}
