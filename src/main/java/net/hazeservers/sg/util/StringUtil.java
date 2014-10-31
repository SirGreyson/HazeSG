/*
 * Copyright (c) ReasonDev 2014.
 * All rights reserved.
 * No part of this project or any of its contents may be reproduced, copied, modified or adapted, without the prior written consent of SirReason.
 */

package net.hazeservers.sg.util;

import net.hazeservers.sg.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    /**
     * Returns the colored version of a String
     *
     * @param input String to color
     * @return Colored version of the String
     */
    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    /**
     * Returns the colored version of a List of Strings
     *
     * @param input String List to color
     * @return Colored version of the List
     */
    public static List<String> colorAll(List<String> input) {
        List<String> output = new ArrayList<>();
        for (String i : input) output.add(color(i));
        return output;
    }

    /**
     * Returns the Double value of a String
     *
     * @param input String to parse
     * @return Double value of the String
     */
    public static double asDouble(String input) {
        return Double.parseDouble(input);
    }

    /**
     * Returns the Integer value of a String
     *
     * @param input String to parse
     * @return Integer value of the String
     */
    public static int asInt(String input) {
        return Integer.parseInt(input);
    }

    /**
     * Returns the Float value of a String
     *
     * @param input String to parse
     * @return Float value of the String
     */
    public static float asFloat(String input) {
        return Float.valueOf(input);
    }

    /**
     * Returns the Byte value of a String
     *
     * @param input String to parse
     * @return Byte value of the String
     */
    public static byte asByte(String input) {
        return Byte.valueOf(input);
    }

    /**
     * Converts seconds to a formatted time String
     *
     * @param input Number of seconds
     * @return Formatted String that conveys a time
     */
    public static String formatTime(int input) {
        //Minutes Value
        return String.valueOf(input / 60) + ":" +
                //Seconds Value
                (String.valueOf(input % 60).length() == 1 ? "0" + String.valueOf(input % 60) : String.valueOf(input % 60));
    }

    /**
     * Returns the PotionEffect value of a String
     *
     * @param input String to parse
     * @return PotionEffect value of the String
     */
    public static PotionEffect parseEffect(String input) {
        String[] args = input.split(":");
        return new PotionEffect(PotionEffectType.getByName(args[0]),
                args[1].equals("-1") ? Integer.MAX_VALUE : asInt(args[1]) * 20,
                args.length > 2 ? asInt(args[2]) : 0, true);
    }

    /**
     * Returns a List of PotionEffects from a String List
     *
     * @param input String List to parse
     * @return PotionEffect List from the List of Strings
     */
    public static List<PotionEffect> parseEffectList(List<String> input) {
        List<PotionEffect> output = new ArrayList<>();
        for (String i : input) output.add(parseEffect(i));
        return output;
    }

    /**
     * Returns the ItemStack value of a String
     *
     * @param input String to parse
     * @return ItemStack value of the String
     */
    public static ItemStack parseItem(String input) {
        String[] args = input.split(" ");
        ItemStack output = new ItemStack(Material.valueOf(args[0].split(":")[0]), args.length < 2 ? 1 : asInt(args[1]));
        if (args[0].contains(":")) output.getData().setData(asByte(args[0].split(":")[1]));
        if (args.length < 3) return output;
        ItemMeta meta = output.getItemMeta();
        if (!args[2].equalsIgnoreCase("NONE")) meta.setDisplayName(color(args[2].replaceAll("_", " ")));
        if (args.length > 3) {
            String[] args2 = args[3].split(",");
            for (int i = 0; i < args2.length; i++) {
                String[] ench = args2[i].split("/");
                meta.addEnchant(Enchantment.getByName(ench[0]), ench.length < 2 ? 1 : asInt(ench[1]), true);
            }
        }
        output.setItemMeta(meta);
        return output;
    }

    /**
     * Returns a List of ItemStacks from a String List
     *
     * @param input String List to parse
     * @return ItemStack List from the List of Strings
     */
    public static List<ItemStack> parseItemList(List<String> input) {
        List<ItemStack> output = new ArrayList<>();
        for (String i : input) output.add(parseItem(i));
        return output;
    }

    /**
     * Returns the Location value of a String
     *
     * @param input String to parse
     * @return Location value of the String
     */
    public static Location parseLocation(String input) {
        String[] args = input.split(",");
        if (args.length < 4) Messaging.printErr("Error parsing Location. Location String invalid!", input);
        else
            return new Location(Bukkit.getWorld(args[0]), asInt(args[1]), asInt(args[2]), asInt(args[3]), asFloat(args[4]), asFloat(args[5]));
        return null;
    }

    /**
     * Returns a List of Locations from a String List
     *
     * @param input String List to parse
     * @return Location List from the List of Strings
     */
    public static List<Location> parseLocationList(List<String> input) {
        List<Location> output = new ArrayList<Location>();
        for (String i : input) output.add(parseLocation(i));
        return output;
    }

    /**
     * Returns the String value of a Location
     *
     * @param input Location to parse
     * @return String value of the Location
     */
    public static String getLocString(Location input) {
        return input.getWorld().getName() + "," + input.getBlockX() + "," + input.getBlockY() + "," + input.getBlockZ() + "," + input.getYaw() + "," + input.getPitch();
    }

    /**
     * Returns a List of Strings from a Location List
     *
     * @param input Location List to parse
     * @return String List from the List of Locations
     */
    public static List<String> getLocStringList(List<Location> input) {
        List<String> output = new ArrayList<String>();
        for (Location l : input) output.add(getLocString(l));
        return output;
    }

    public static Vector parseVector(String input) {
        String[] args = input.split(",");
        return new Vector(asDouble(args[0]), asDouble(args[1]), asDouble(args[2]));
    }

    public static List<Vector> parseVectorList(List<String> input) {
        List<Vector> output = new ArrayList<>();
        for (String i : input) {
            output.add(parseVector(i));
        }
        return output;
    }

    public static List<String> getVectorStringList(List<Vector> input) {
        List<String> output = new ArrayList<>();
        for (Vector i : input) output.add(i.toString());
        return output;
    }
}
