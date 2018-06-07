package com.helloword.main;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.helloword.interfaces.CountLinsenter;

public class GuiContorl extends JFrame implements CountLinsenter{

	/**
	 * java桌面UI控件布局
	 */
	private static final long serialVersionUID = 4523640833472814634L;
	private JLabel urlCountLable;
	private JLabel PicCountLable;
	
	public void showView(){
		setSize(400, 300);
		
		GridBagConstraints gbc=new GridBagConstraints();
		
		JPanel rootUrl = new JPanel();
		rootUrl.add(new JLabel("检测URL数量："));
		urlCountLable=new JLabel("0");
		rootUrl.add(urlCountLable);
				
		JPanel rootPicture = new JPanel();
		rootPicture.add(new JLabel("检测图片数量："));
		PicCountLable=new JLabel("0");
		rootPicture.add(PicCountLable);
		
		JPanel rootCancle = new JPanel();
		JButton jButton=new JButton("取消");
		jButton.setEnabled(true);
		jButton.setBounds(100,150,80,50);
		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//关闭当前界面  
                dispose();
			}
		});
		rootCancle.add(jButton);
		
		//设置布局管理  
        this.setLayout(new GridLayout(3, 1));//网格式布局 
		
      	add(rootUrl);
      	add(rootPicture);
      	add(rootCancle);
      	setDefaultCloseOperation(EXIT_ON_CLOSE);
      	setVisible(true);
      	
      	
      //关键代码，设置按钮位置 
      	rootUrl.setBounds((this.getWidth()-rootUrl.getWidth()-5)/2,(this.getHeight()-28-rootUrl.getHeight())/2,
      			rootUrl.getWidth(),rootUrl.getHeight());
	}

	@Override
	public void urlCountChanged(int count) {
		urlCountLable.setText(count+"");
	}

	@Override
	public void picCountChanged(int count) {
		PicCountLable.setText(""+count);
		
	}
}
