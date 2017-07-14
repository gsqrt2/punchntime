package punchntime;

import javax.swing.*;
import java.awt.BorderLayout;

public class TimeKeeper extends JFrame {
	
	public TimeKeeper(){
		
		initGui();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TimeKeeper();
	}
	
	private void initGui()
	{
		mainPanel = new JPanel(new BorderLayout());
		timeLabel = new JLabel("00:00:00");
		mainPanel.add(timeLabel, BorderLayout.CENTER);
		
		JLabel projectListDummy = new JLabel("projectList");
		mainPanel.add(projectListDummy, BorderLayout.LINE_START);
		
		getContentPane().add(mainPanel);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
	}

	private JLabel timeLabel;
	private JPanel mainPanel;
}
