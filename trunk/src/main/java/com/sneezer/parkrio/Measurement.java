package com.sneezer.parkrio;

import java.util.HashMap;
import java.util.Map;

public class Measurement {
	public static String[] kindList= new String[] {"elec","hotwater","heat","water","gas"};
	
	private double elec = 0;	// 전기
	private double hotwater = 0;	// 온수
	private double heat = 0;	// 난방
	private double water = 0;	// 수도
	private double gas = 0;	// 가스
	
	public int size() {
		return kindList.length;
	}
	public double getElec() {
		return elec;
	}
	public void setElec(double elec) {
		this.elec = elec;
	}
	public double getHotwater() {
		return hotwater;
	}
	public void setHotwater(double hotwater) {
		this.hotwater = hotwater;
	}
	public double getHeat() {
		return heat;
	}
	public void setHeat(double heat) {
		this.heat = heat;
	}
	public double getWater() {
		return water;
	}
	public void setWater(double water) {
		this.water = water;
	}
	public double getGas() {
		return gas;
	}
	public void setGas(double gas) {
		this.gas = gas;
	}
	public String toString() {
		return (new String(this.elec+";"+this.hotwater+";"+this.heat+";"+this.water+";"+this.gas));
	}
	public Map<String, Double> getObject(){
		Map<String, Double> resultMap = new HashMap<String, Double>();
		resultMap.put("Elec", elec);
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
