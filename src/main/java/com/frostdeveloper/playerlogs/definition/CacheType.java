package com.frostdeveloper.playerlogs.definition;

public enum CacheType
{
	UPDATE_CD("update-cd"),
	METRICS_CD("metrics-cd"),
	RAM_CD("ram-cd"),
	TPS_CD("tps-cd");
	
	private final String key;
	
	CacheType(String key)
	{
		this.key = key;
	}
	
	public String getKey()  { return key; }
}
