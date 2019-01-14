package com.autoabm.componentes;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class EnteroTextField extends JTextField implements KeyListener {
	
	private static final long serialVersionUID = -8791804016399840179L;
	private DecimalFormat formatter = new DecimalFormat("#,###.##");
	private String decimalSeparator;
	private String groupingSeparator;
	private Border border;

	public EnteroTextField() {
		this.addKeyListener(this);
		this.groupingSeparator = formatter.getDecimalFormatSymbols().getGroupingSeparator()+"";
		this.decimalSeparator = formatter.getDecimalFormatSymbols().getDecimalSeparator()+"";
		this.border = this.getBorder(); 
	}

	public void setText(String str) {

		int selstart = super.getSelectionStart();
		int lentoend = super.getText().length() - selstart;

		try {
			super.setText(formatter.format(Long.parseLong(str)));
		} catch (Exception e) {
			super.setText("0");
		}
		

		int newselstart = super.getText().length() - lentoend;
		super.select(newselstart, 0);
	}
	
	public void setValue(Long i) {
		if(i == null) this.setText("0");
		else this.setText(i+"");
	}
	
	public void setValue(Integer i) {
		setValue(new Long(i));
	}
	
	public Long getLongValue(){
		try {
			return Long.parseLong(this.getText());
		} catch (NumberFormatException e) {
			return 0l;
		}
	}
	
	public int getIntValue(){
		return getLongValue().intValue();
	}
	
	private void error() {
		this.setBorder(BorderFactory.createLineBorder(Color.RED));
		new Timer().schedule(new TimerTask() {
		    @Override
		    public void run() {
		    	EnteroTextField.this.setBorder(border);
		    }
		}, 300);
	}

	@Override
	public String getText() {
		String str = super.getText().replace(groupingSeparator, "");
		str = str.replace(decimalSeparator, ".");
		return str;
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		char c = keyEvent.getKeyChar();
		if ((!(Character.isDigit(c))) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
			keyEvent.consume();
			if(c != KeyEvent.VK_BACK_SPACE) error();
		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		char c = keyEvent.getKeyChar();
		if (Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE || c == KeyEvent.VK_LEFT
				|| c == KeyEvent.VK_RIGHT) {
			this.setText(this.getText());			
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

}
