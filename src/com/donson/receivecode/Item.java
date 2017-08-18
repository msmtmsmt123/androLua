package com.donson.receivecode;

/**
 * 服务项目
 * 
 * @author Joshua
 *
 */
public class Item {

	// 项目id
	private int id;
	// 项目名称
	private String name;
	// 项目价格
	private float price;
	// 项目类型
	private ItemType itemType;
	
	public Item(String[] fields) {
		if (fields.length < 4) return;
		this.id = (Integer.valueOf(fields[0]));
		this.name = (fields[1]);
		this.price = (Float.valueOf(fields[2]));
		this.itemType = (ItemType.valueOf(Integer.valueOf(fields[3])));
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * @return the itemType
	 */
	public ItemType getItemType() {
		return itemType;
	}
}

