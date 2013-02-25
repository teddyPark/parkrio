package com.sneezer.parkrio;

public class Measurement {
	private int elect = 0;	// 전기
	private int hotwater = 0;	// 온수
	private int heat = 0;	// 난방
	private int water = 0;	// 수도
	private int gas = 0;	// 가스
	
	public int getElect() {
		return elect;
	}
	public void setElect(int elect) {
		this.elect = elect;
	}
	public int getHotwater() {
		return hotwater;
	}
	public void setHotwater(int hotwater) {
		this.hotwater = hotwater;
	}
	public int getHeat() {
		return heat;
	}
	public void setHeat(int heat) {
		this.heat = heat;
	}
	public int getWater() {
		return water;
	}
	public void setWater(int water) {
		this.water = water;
	}
	public int getGas() {
		return gas;
	}
	public void setGas(int gas) {
		this.gas = gas;
	}
}
