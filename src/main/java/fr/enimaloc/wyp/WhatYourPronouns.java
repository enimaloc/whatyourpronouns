package fr.enimaloc.wyp;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WhatYourPronouns {
	public static final String MOD_ID = "whatyourpronouns";

	// This logger is used to write text to the console and the log file.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private WhatYourPronouns() {
	}

	public static void init() {
		LOGGER.info("Hello Fabric world!");
	}

	//? if >=1.21.1 {
	/*public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}*/
	//? } else {
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
	//? }
}
