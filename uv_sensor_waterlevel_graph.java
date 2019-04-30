
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.fazecast.jSerialComm.SerialPort;

public class testing_of_app {
	static String x1="";
	static String y="";
	static String z="";
	String temp="";
	static SerialPort chosenPort;
	//static int x = 0;

	public static void main(String[] args) {

		// create and configure the window
		JFrame window = new JFrame();
		window.setTitle("Sensor Graph GUI");
		window.setSize(1400, 450);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// create a drop-down box and connect button, then place them at the top of the window
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connectButton);
		window.add(topPanel, BorderLayout.NORTH);

		// populate the drop-down box
		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());

		// create the line graph
		XYSeries series = new XYSeries("Water Sensor Readings");
		XYSeries series1 = new XYSeries("WSR");
		XYSeriesCollection dataset = new XYSeriesCollection();

		JFreeChart chart = ChartFactory.createXYLineChart("Water Sensor Readings", "Serial no", "CM", dataset);
		ChartPanel chartpanel =new ChartPanel(chart);
		chartpanel.setPreferredSize(new java.awt.Dimension(1200,400));
		XYPlot plot = (XYPlot) chart.getPlot();
		NumberAxis domainAxis = new NumberAxis();
		NumberAxis rangeAxis = new NumberAxis();
		domainAxis.setAutoRange(false);
		domainAxis.setLowerBound(0);
		//domainAxis.setUpperBound(99999);
		domainAxis.setTickUnit(new NumberTickUnit(2));
		rangeAxis.setRange(0, 1000);
		rangeAxis.setTickUnit(new NumberTickUnit(50));
		//plot.setDomainAxis(domainAxis);
		plot.setRangeAxis(rangeAxis);
		//window.add(new ChartPanel(chart), BorderLayout.CENTER);
		//topPanel.add(chartpanel);
		JScrollPane sc=new JScrollPane(chartpanel);
		topPanel.add(sc);
		window.setContentPane(topPanel);
		//window.setContentPane(sc);

		// configure the connect button and use another thread to listen for data
		connectButton.addActionListener(new ActionListener(){
			Timer timer = new Timer(3000, new ActionListener() {
			@Override public void actionPerformed(ActionEvent arg0) {
				if(connectButton.getText().equals("Connect")) {
					// attempt to connect to the serial port
					chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
					chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
					if(chosenPort.openPort()) {
						connectButton.setText("Disconnect");
						portList.setEnabled(false);
					}

					// create a new thread that listens for incoming text and populates the graph
					Thread thread = new Thread(){
						@Override public void run() {
							Scanner scanner = new Scanner(chosenPort.getInputStream());
							while(scanner.hasNextLine()) {
								try {
									String line = scanner.nextLine();
				               		System.out.println(line);
							        x1=line.substring(1,6);
							        y=line.substring(7,12);
							        z=line.substring(13,18);
									series.add(Double.parseDouble(x1),Double.parseDouble(z)*10);
									series1.add(Double.parseDouble(x1),Double.parseDouble(y)*10);
									dataset.addSeries(series);
									dataset.addSeries(series1);
									window.repaint();
								} catch(Exception e) {}
							}
							scanner.close();
						}
					};
					thread.start();
				} else {
					// disconnect from the serial port
					chosenPort.closePort();
					portList.setEnabled(true);
					connectButton.setText("Connect");
					series.clear();
					//x = 0;
				}
			}
		});

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		// show the window
		window.setVisible(true);
	}

}
	/*
	public void paint(Graphics g)
	{

	}
  */
