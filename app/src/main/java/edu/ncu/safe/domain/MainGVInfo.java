package edu.ncu.safe.domain;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;

public class MainGVInfo {

	public static final int[] Re = { R.drawable.phoneprotector, R.drawable.gprsflows,
			R.drawable.databackup, R.drawable.communication,
			R.drawable.softwaremanager, R.drawable.systemfaster,
			R.drawable.softwlock,R.drawable.set};
	public static final String[] ti = { "手机防盗", "流量管理", "数据备份", "通讯卫士", "软件管理",
			"系统加速","程序锁","设置" };
	public static final String[][] ano = { {"防护未开启","正在防护"}, {"未设置流量",""}, {""}, {""},{""}, {""},{""}, {""}};

	private List<Integer> icons = new ArrayList<Integer>();
	private List<String> titles = new ArrayList<String>();
	private List<Integer> anotations = new ArrayList<Integer>();
	

	public int getSize() {
		return titles.size();
	}

	public List<Integer> getIcons() {
		return icons;
	}

	public void setIcons(List<Integer> icons) {
		this.icons = icons;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public List<Integer> getAnotations() {
		return anotations;
	}

	public void setAnotations(List<Integer> anotations) {
		this.anotations = anotations;
	}

	public void resetIcons(int[] r) {
		icons.clear();
		for (int i = 0; i < r.length; i++) {
			icons.add(r[i]);
		}
	}

	public void resetTitles(String[] t) {
		titles.clear();
		for (int i = 0; i < t.length; i++) {
			titles.add(t[i]);
		}
	}

	public void resetTAnotations(int[] a) {
		anotations.clear();
		for (int i = 0; i < a.length; i++) {
			anotations.add(a[i]);
		}
	}
}
