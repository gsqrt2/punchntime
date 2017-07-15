package punchntime;

import javax.swing.*;
import java.awt.BorderLayout;
import java.sql.Timestamp;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TimeKeeper extends JFrame {
	
	public TimeKeeper(){
		super("Punch 'N Time!!!");
		displayUpdateRunnable = new Runnable(){
			public void run(){
				while(clockRunning)
				{
					try{
						long elapsedTime = (System.currentTimeMillis()-startTime)/1000;
						timeLabel.setText(""+elapsedTime);
						Thread.sleep(1000);
					}catch(Exception e){System.out.println("Exception with thread sleeping in updateTimeDisplay: "+e);}
				}
			}
		};
		initGui();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("starting");
		new TimeKeeper();
	}
	
	private void initGui()
	{
		mainPanel = new JPanel(new BorderLayout());
		timeLabel = new JLabel("00:00:00");
		mainPanel.add(timeLabel, BorderLayout.CENTER);
		
		
		
		startButton = new JButton("start");
		stopButton = new JButton("stop");
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(startButton);
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if(!clockRunning)
				{
					startTimeKeeper();
				}
			}
		});
		
		buttonPanel.add(stopButton);
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if(clockRunning)
				{
					stopTimeKeeper();
				}
			}	
		});
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
		
		
		addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
            	
            	if(clockRunning)
            		stopTimeKeeper();
            	
                ((JFrame)(e.getComponent())).dispose();
            }
        });
		getContentPane().add(mainPanel);
		pack();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void startTimeKeeper()
	{
		startTime = System.currentTimeMillis();
		clockRunning = true;
		startTimeDisplay();
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		System.out.println("started at: "+new Timestamp(System.currentTimeMillis()).toString());
	}
	
	private void stopTimeKeeper()
	{
		clockRunning = false;
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		System.out.println("stopped at: "+new Timestamp(System.currentTimeMillis()).toString());
		System.out.println("time span: "+(System.currentTimeMillis()-startTime));
	}
	
	private void startTimeDisplay()
	{
		timeDisplayThread = new Thread(displayUpdateRunnable, "time Display Thread");
		timeDisplayThread.start();
	}
	
	

	private JLabel timeLabel;
	private JPanel mainPanel, buttonPanel;
	private JButton startButton, stopButton;
	private boolean clockRunning = false;
	private long startTime;
	private Runnable displayUpdateRunnable;
	private Thread timeDisplayThread;
}
