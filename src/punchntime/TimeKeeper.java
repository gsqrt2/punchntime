package punchntime;

import javax.swing.*;
import java.awt.BorderLayout;
import java.sql.Timestamp;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TimeKeeper extends JFrame {
	
	public TimeKeeper(){
		super("Punch 'N Time!!!");
		displayUpdateRunnable = new Runnable(){
			public void run(){
				while(clockRunning)
				{
					try{
						long elapsedTime = (System.currentTimeMillis()-startTime)/1000;
						timeLabel.setText(""+secondsFormat());
						Thread.sleep(1000);
					}catch(Exception e){System.out.println("Exception with thread sleeping in updateTimeDisplay: "+e);}
				}
			}
		};
		initGui();
		
		/*static user id, to delete after login*/
		userId = 1;
		projectId = 1;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("starting");
		new TimeKeeper();
	}
	
	private void initGui()
	{
		mainPanel = new JPanel(new BorderLayout());
		timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		timeLabel = new JLabel("00:00:00");
		timePanel.add(timeLabel);
		mainPanel.add(timePanel, BorderLayout.CENTER);
		
		infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		projectTaskLabel = new JLabel("project1 - task1");
		infoPanel.add(projectTaskLabel);
		mainPanel.add(infoPanel, BorderLayout.PAGE_START);
		
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
		//setPreferredSize(new Dimension(250, 120));
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
		stopTime = System.currentTimeMillis();
		clockRunning = false;
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		System.out.println("stopped at: "+new Timestamp(System.currentTimeMillis()).toString());
		System.out.println("time span: "+(System.currentTimeMillis()-startTime));
		updateDb(userId, projectId);
		
	}
	
	private void startTimeDisplay()
	{
		timeDisplayThread = new Thread(displayUpdateRunnable, "time Display Thread");
		timeDisplayThread.start();
	}
	
	private String secondsFormat()
	{
		long currentTime = System.currentTimeMillis()-startTime;
		int seconds = (int) (currentTime / 1000) % 60 ;
		int minutes = (int) ((currentTime / (1000*60)) % 60);
		int hours   = (int) ((currentTime / (1000*60*60)) % 24);

		return String.format(
			      "%02d:%02d:%02d",
			      hours, minutes, seconds);
	}
	
	private void updateDb(int userId, int projectId)
	{
		if(pntConnector == null)
			pntConnector = new PntConnector();
		
		Connection pntConnection = pntConnector.pntConnect();
		if(pntConnection!=null)
		{
			System.out.println("db connect!!!!");
			PreparedStatement preparedStatement = null;
			try
			{
				String insertTableSQL = "INSERT INTO pnt_sessions"
					+ "(pnt_session_user, pnt_session_start, pnt_session_stop, pnt_session_project) VALUES"
					+ "(?,?,?,?)";
				preparedStatement = pntConnection.prepareStatement(insertTableSQL);
				
				preparedStatement.setInt(1, userId);
				preparedStatement.setTimestamp(2, new Timestamp(startTime));
				preparedStatement.setTimestamp(3, new Timestamp(stopTime));
				preparedStatement.setInt(4, projectId);
				
				preparedStatement.executeUpdate();
				
				System.out.println("updateDb query executed!");
				
			}
			catch(SQLException e)
			{
				System.out.println("Exception inserting session: "+e.getMessage());
			}
			finally
			{
				if(preparedStatement != null)
				{
					try
					{
						preparedStatement.close();
					}catch(SQLException e){System.out.println("Exception closing preparedStatement: "+e.getMessage());}
				}

				if(pntConnection != null)
				{
					try
					{
						pntConnection.close();
					}catch(SQLException e){System.out.println("Exception closing pntConnection: "+e.getMessage());}
				}
			}
		}
		else
		{
			System.out.println("pntConnector could not connect to the database.");
		}
	}

	private JLabel timeLabel, projectTaskLabel;
	private JPanel mainPanel, buttonPanel, timePanel, infoPanel;
	private JButton startButton, stopButton;
	private boolean clockRunning = false;
	private long startTime, stopTime;
	private Runnable displayUpdateRunnable;
	private Thread timeDisplayThread;
	private PntConnector pntConnector;
	private int userId, projectId;
}
