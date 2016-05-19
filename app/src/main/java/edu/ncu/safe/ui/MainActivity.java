package edu.ncu.safe.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.slidingmenu.lib.SlidingMenu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.MainGVAdapter;
import edu.ncu.safe.adapter.MainMenuAdapter;
import edu.ncu.safe.domain.MainGVInfo;
import edu.ncu.safe.domain.MainMenuInfo;
import edu.ncu.safe.domain.VersionInfo;
import edu.ncu.safe.engine.DownLoadFile;
import edu.ncu.safe.engine.LoadLatestVersionInfo;

public class MainActivity extends Activity implements
		View.OnClickListener {
	public static final String TAG = "MainActivity";
	private static final int SHOWUPDATEDIALOG = 0;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case SHOWUPDATEDIALOG:
					showUpdateDialog();
					break;
				default:
					break;
			}
		};
	};
	// 主界面控件
	private ImageView user;// 开启侧滑菜单
	private ImageView set;// 右上角设置图标

	private GridView gv;// 主界面下方的六个选项
	private MainGVAdapter adapter;// gv的数据适配器

	// 滑动菜单控件
	private SlidingMenu menu;// 侧滑菜单
	private View menuView;// 侧滑菜单的view
	private LinearLayout linearLayout;
	private TextView protectTime;// 侧滑菜单第一行的保护时间控件
	private ListView menuList;// 侧滑菜单的listview
	private MainMenuAdapter menuAdapter;// menuList的适配器

	// 加载最新版本信息
	private LoadLatestVersionInfo latestVersionInfo = new LoadLatestVersionInfo(
			this);
	private VersionInfo versionInfo;
	private ProgressDialog progressDialog;

	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		log("onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 初始化各个成员函数
		user = (ImageView) this.findViewById(R.id.user);
		set = (ImageView) this.findViewById(R.id.set);
		gv = (GridView) this.findViewById(R.id.main_gv);

		menuView = LayoutInflater.from(this).inflate(
				R.layout.besidelayout_mainmenu, null);

		linearLayout = (LinearLayout) menuView.findViewById(R.id.menu_firstline);
		protectTime = (TextView) menuView
				.findViewById(R.id.main_menu_protecttime);
		menuList = (ListView) menuView.findViewById(R.id.main_menu_lv);

		sp = getSharedPreferences("conf", Context.MODE_MULTI_PROCESS);
		menu = new SlidingMenu(this);
		menuAdapter = new MainMenuAdapter(this,getMainMenuInfo());
		adapter = new MainGVAdapter(this,getMainGVInfo());

//		初始化slidingmenu配置
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置触摸屏幕的模式
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 设置滑动菜单视图的宽度
		menu.setFadeDegree(0.35f);// 设置渐入渐出效果的值
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(menuView);// 为侧滑菜单设置布局,将菜单布局添加到slidingmenu里

		// 给gridview添加数据适配器和项目点击事件
		gv.setAdapter(adapter);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉gridview点击后出现的黄色边框
		// 给菜单里的listview添加数据适配器和项目点击事件
		menuList.setAdapter(menuAdapter);

		gv.setOnItemClickListener(new GridViewItemOnClickListener());
		menuList.setOnItemClickListener(new ListViewItemOnClickListener());
		linearLayout.setOnClickListener(this);// 给菜单栏里的第一行添加点击事件
		user.setOnClickListener(this);
		set.setOnClickListener(this);
		//添加layoutanimation
		gv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.gv_item_appear));
		// 检查是否有新版本信息，有则弹出升级对话框
		newVersion();
	};

	@Override
	protected void onStart() {
		log("onStart");
		super.onStart();
		//展现动画
		gv.startLayoutAnimation();
		//更新界面信息
		adapter.setInfo(getMainGVInfo());
		adapter.notifyDataSetChanged();
		menuAdapter.setList(getMainMenuInfo());
		menuAdapter.notifyDataSetChanged();
	}

	//获取grideview里面的数据信息
	private MainGVInfo getMainGVInfo() {
		MainGVInfo info = new MainGVInfo();
		info.resetIcons(MainGVInfo.Re);
		info.resetTitles(MainGVInfo.ti);
		//改变条目文本下面的注释
		int[] ano = new int[MainGVInfo.ti.length];
		SharedPreferences sp = this.getSharedPreferences(
				PhoneLostProtectActivity.SHAREPERFERENCESNAME,
				Context.MODE_MULTI_PROCESS);
		if (sp.getBoolean(PhoneLostProtectActivity.ISINPROTECTING, false)) {
			ano[0] = 1;
		}
		sp = this.getSharedPreferences(
				FlowsProtectorActivity.FLOWSSHAREDPREFERENCES,
				Context.MODE_MULTI_PROCESS);
		if (sp.getLong(FlowsProtectorActivity.FLOWSTOTAL, 0) > 0) {
			ano[1] = 1;
		}
		info.resetTAnotations(ano);
		return info;
	}

	//获取侧滑菜单里listview的信息
	private ArrayList<MainMenuInfo> getMainMenuInfo() {
		ArrayList<MainMenuInfo> list = new ArrayList<MainMenuInfo>();
		for (int i = 0; i < MainMenuInfo.re.length; i++) {
			MainMenuInfo info = new MainMenuInfo();
			info.setImgID(MainMenuInfo.re[i]);
			info.setTitle(MainMenuInfo.titles[i]);
			info.setAnotation(MainMenuInfo.anotations[i]);
			info.setHasDirection(MainMenuInfo.hasdirection[i]);
			list.add(info);
		}
		return list;
	}

	//从服务器中获取最新版本
	private void newVersion() {
		new Thread() {
			@Override
			public void run() {
				// 获取新版本信息
				try {
					versionInfo = latestVersionInfo.getVersionInfo();
					log("获取版本信息成功");
				} catch (Exception e) {
					e.printStackTrace();
					log("获取版本信息失败");
					return;
				}
				String currentVersion;
				try {
					currentVersion = MainActivity.this.getPackageManager().getPackageInfo(
							getPackageName(), 0).versionName;
					log("获取本地版本成功");
				} catch (NameNotFoundException e) {
					e.printStackTrace();
					log("获取本地版本信息失败");
					return;
				}

				if (!currentVersion.equals(versionInfo.getVerion())) {
					// 版本号不同，有新的版本信息
					log("版本号不同，有新的版本信息");
					handler.sendEmptyMessage(SHOWUPDATEDIALOG);
				} else {
					// 版本号相同，无须更新
					log("版本号相同，无须更新");
				}
			}
		}.start();

	}

	private void showUpdateDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("新版本:" + versionInfo.getVerion());
		builder.setMessage(versionInfo.getDescription());

		builder.setPositiveButton("立即升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setTitle("正在下载护机宝" + versionInfo.getVerion());
				progressDialog.setCancelable(false);
				progressDialog.show();
				new Thread() {
					@Override
					public void run() {
						log("进入下载。。。");
						try {
							String path = Environment
									.getExternalStorageDirectory()
									+ "/360safe.apk";
							File file = DownLoadFile.downloadFile(
									versionInfo.getDownloadUrl(), path,
									progressDialog);
							if (file == null) {
								log("file is null");
								return;
							}
							log("下载完成");
							install(file);
						} catch (IOException e) {
							e.printStackTrace();
							log("下载失败");
						}
					}
				}.start();
			}
		});
		builder.setNegativeButton("稍后升级", null);
		builder.show();
	}


	//安装某个文件软件
	private void install(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
		finish();
	}

	class GridViewItemOnClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> viewGroup, View view,
								int position, long arg3) {
			switch (position) {
				case 0:// 手机防盗
					SharedPreferences sp = MainActivity.this.getSharedPreferences(
							PhoneLostProtectActivity.SHAREPERFERENCESNAME,
							Context.MODE_MULTI_PROCESS);
					boolean hasSetPWD = sp.getBoolean(
							PhoneLostProtectActivity.HASSETPWD, false);
					if (hasSetPWD) {// 已经设置过密码
						showInputPWDDialog();
					} else {// 还未设置密码
						showSetPWDDialog();
					}
					break;
				case 1:// 流量监控
					toAntherActivity(FlowsProtectorActivity.class);
					break;
				case 2://

					break;
				case 3:// 通讯卫士
					toAntherActivity(CommunicationProtectorActivity.class);
					break;
				case 4://
					break;
				case 5://
					break;
				default:
					break;
			}
		}

	}

	class ListViewItemOnClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> viewGroup, View view,
								int position, long arg3) {
			switch (position) {
				case 0:// wifi网络环境，点击无事件
					break;
				case 1:// 安全扫码
					break;
				case 2:// 通讯录优化
					break;
				case 3:// 骚扰电话识别
					break;
				case 4:// 保修查询
					break;
				default:
					break;
			}
		}
	}

	// 上方两个点击事件 user set
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.user:
				menu.toggle();
				break;
			case R.id.set:
				break;
			case R.id.menu_firstline:
				menu.toggle();// 动态断定主动封闭或开启SlidingMenu
				break;
			default:
				break;
		}
	}

	private void showSetPWDDialog() {
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_passwordregister, null);
		final EditText pwd = (EditText) v.findViewById(R.id.pwd);
		final EditText pwdAgain = (EditText) v.findViewById(R.id.pwdagain);
		Button btnOK = (Button) v.findViewById(R.id.yes);
		Button btnCancle = (Button) v.findViewById(R.id.no);
		btnOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pd = pwd.getText().toString().trim();
				String pdAgain = pwdAgain.getText().toString().trim();
				if (pd.equals(pdAgain)) {
					SharedPreferences sp = MainActivity.this
							.getSharedPreferences(
									PhoneLostProtectActivity.SHAREPERFERENCESNAME,
									Context.MODE_MULTI_PROCESS);
					Editor editor = sp.edit();
					editor.putString(PhoneLostProtectActivity.ENTERPWD, pd);
					editor.putBoolean(PhoneLostProtectActivity.HASSETPWD, true);
					editor.apply();
					dialog.dismiss();
					// 进入界面
					toAntherActivity(PhoneLostProtectActivity.class);
					dialog.dismiss();
					return;
				} else {
					makeToast("两次密码不相同，请重新输入");
				}
			}
		});

		btnCancle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(v);
		dialog.show();
	}

	public void showInputPWDDialog() {
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View v = LayoutInflater.from(this).inflate(R.layout.dialog_passwordenter, null);
		final EditText pwd = (EditText) v.findViewById(R.id.pwd);
		Button btnOK = (Button) v.findViewById(R.id.enter_yes);
		Button btnCancle = (Button) v.findViewById(R.id.enter_no);
		btnOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sp = MainActivity.this.getSharedPreferences(
						PhoneLostProtectActivity.SHAREPERFERENCESNAME,
						Context.MODE_MULTI_PROCESS);
				String enterPwd = sp.getString(
						PhoneLostProtectActivity.ENTERPWD, "");
				String pd = pwd.getText().toString().trim();
				if (enterPwd.equals(pd)) {// 正确输入密码，进入界面
					toAntherActivity(PhoneLostProtectActivity.class);
					dialog.dismiss();
					return;
				}
				makeToast("密码输入错误！");
			}
		});

		btnCancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(v);
		dialog.show();
	}

	//实现跳转
	private void toAntherActivity(Class cls){
		Intent intent = new Intent();
		intent.setClass(this, cls);
		startActivity(intent);
		//切换动画
		overridePendingTransition(R.anim.activit3dtoleft_in,
				R.anim.activit3dtoleft_out);
	}
	//显示toast
	private void makeToast(String message){
		Toast.makeText(this,message, Toast.LENGTH_LONG).show();
	}
	//显示log
	private void log(String message){
		Log.i(TAG, message);
	}
}