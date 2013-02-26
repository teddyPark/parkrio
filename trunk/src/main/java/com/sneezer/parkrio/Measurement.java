package com.sneezer.parkrio;

import java.util.HashMap;
import java.util.Map;

public class Measurement {
	private float elect = 0;	// ����
	private float hotwater = 0;	// �¼�
	private float heat = 0;	// ����
	private float water = 0;	// ����
	private float gas = 0;	// ����
	
	public float getElect() {
		return elect;
	}
	public void setElect(float elect) {
		this.elect = elect;
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
		return (new String(this.elect+";"+this.hotwater+";"+this.heat+";"+this.water+";"+this.gas));
	}
	public Map<String, Float> getObject(){
		Map<String, Float> resultMap = new HashMap<String,Float>();
		resultMap.put("Elect", elect);
		resultMap.put("Hotwater", hotwater);
		resultMap.put("Heat", heat);
		resultMap.put("Water", water);
		resultMap.put("Gas", gas);
		
		return resultMap;
	}
}
