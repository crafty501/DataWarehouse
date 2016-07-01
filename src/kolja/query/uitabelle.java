package kolja.query;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import antlr.collections.List;
import data.DB2ConnectionManager;

public class uitabelle extends JFrame{

	String[] titel;
	String[][] data;
	DB2ConnectionManager mgr;
	JSlider slider;
	JTable table;
	JProgressBar progressBar;
	long maxdays;
	int size;
	int period;
	SimpleDateFormat dateFormat;
	JLabel label;
	String Anfrage1 = "SELECT REGION FROM SHOP GROUP BY REGION";
	String[] times_start;
	String[] times_end;
	int AnzahlRegion;
	int progress ;
	private void CreateTitel(){
		
		String Anfrage = "SELECT NAME FROM ARTIKEL";
		ResultSet res;
		try {
			ArrayList<String> titelliste = new ArrayList<String>();
			
			res = mgr.SendQuery(Anfrage, true);
			while (res.next()){
				String S = res.getString(1);
				System.out.println(S);
				titelliste.add(S);
				
			}
			//Ein Array erzeugen
			
			int l = titelliste.size();
			titel = new String[l+2];
			
			
			titel[0]="Ort";
			titel[1]="Zeit";
			
			for(int i = 0 ; i < l ; i++){
				titel[i+2] = titelliste.get(i);
			}
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	private long GetDays(){
		
		try{
			String Anfrage = "SELECT DATUM FROM SALES ORDER BY DATUM ASC";
			ResultSet r = mgr.SendQuery(Anfrage, true);
			r.next();
			String startdate = r.getString(1);
			Anfrage = "SELECT DATUM FROM SALES ORDER BY DATUM DESC";
			r = mgr.SendQuery(Anfrage, true);
			r.next();
			String enddate = r.getString(1);
			
			Date d = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	        long start = 0;
	        long end = 0;
			try {
				start = formatter.parse(startdate).getTime();
				end   = formatter.parse(enddate).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return (end - start)/(1000*60*60*24);
		}catch(SQLException e){
			
		}
		return -1;
		
		
	}
	
	private void GenerateDates(){
		String startDate = "01.01.2015";
		String endDate = "";
		int size = (int)(maxdays / period);
		times_start = new String[size + 1 ];
		times_end = new String[size + 1];
		int i = 0;
		for(int p = 0; p < maxdays ; p = p + period ){
			Date date;
			try {
				date = dateFormat.parse(startDate);
				long time = date.getTime() + (86400000l * (long)period) ;
				Date plusdays = new Date(time);
				endDate = dateFormat.format(plusdays);
				times_start[i] = startDate;
				times_end[i] = endDate;
				startDate = endDate;
				i++;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private ResultSet gibRegionAnzahl(){
		try{
		ResultSet res = mgr.SendQuery(Anfrage1, true);
		AnzahlRegion = 0;
		while (res.next()) {
			AnzahlRegion++;
		}
		int size = (int)((float)maxdays / (float)period);
		if(maxdays % period > 0 ){
			size++;
		}
		data = new String[(int) maxdays][titel.length];
		res = mgr.SendQuery(Anfrage1, true);
		return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void CreateData(boolean update){
		
		assert size > 0 : "Vorbedingung verletzt! size muss grösser null sein";
		
		
		data = new String[(int) (maxdays)][titel.length];
		ResultSet res = this.gibRegionAnzahl();
		GenerateDates();
		int zeile = 0;
		try{
		while(res.next()){
			String Region = res.getString(1);
			int c = 0;
			for(int p = 0; p < maxdays ; p = p + period ){		
					for ( int i = 2 ; i < titel.length; i++){
						String Product = titel[i];
						QueryThread t = new QueryThread();
						t.SetParameter(mgr,Region,Product,times_start[c],times_end[c],zeile,i);
						t.SetUIParam(table);
						t.SetData(data);
						t.SetUpdate(update);
						t.setProgressBar(progressBar);
						t.start();
						
					}
					c++;
				zeile++;
			}
		}}catch(SQLException e ){
			System.out.println(e.getMessage());
		}
		
}

	
	
private void UpdateUI(){
		
	
	
	for(int i = 0 ; i < table.getRowCount() ; i++){
		for(int j = 0 ; j < table.getColumnCount(); j++){
		table.setValueAt("", i, j);
		}
	}
	CreateTitel();
	CreateData(true);
	
	
	}
	
	
public uitabelle(DB2ConnectionManager m){
		super();

		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		this.mgr = m;
		
		maxdays = this.GetDays();
		size =(int)(maxdays / 149);
		period = 149;
		
		CreateTitel();
		
		this.setLayout(new BorderLayout());
		
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
        progressBar.setMaximum(titel.length * size);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		this.add(progressBar,BorderLayout.SOUTH);
		
		
		CreateData(false);
		
		
		
		
		slider = new JSlider();
		slider.setMinimum(1);    				  //stellt den Minimalwert auf 0 ein
		slider.setMaximum((int) this.GetDays());  //stellt den Maximalwert auf 150 ein
		slider.setValue(140);
		
		slider.setMinorTickSpacing(10); //Abstände im Feinraster
		slider.setMajorTickSpacing(20); //Abstände im Großraster
		slider.setOrientation(JSlider.VERTICAL);
		slider.setPaintTicks(true);    //Striche werden angezeigt
		slider.setPaintLabels(true);  //Zahlen werden nicht angezeigt
		slider.setPaintTrack(true);    //Balken wird angezeigt
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				String S = String.valueOf(slider.getValue());
				label.setText(S+" Tage");
			}
		});
		
		label = new JLabel();
		String S = String.valueOf(slider.getValue());
		label.setText(S+" Tage");
		
		
		JButton pushbutton = new JButton();
		pushbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						period = slider.getValue();
						
						
						
						int size = (int)((float)maxdays / (float)period);
						if(maxdays % period > 0 ){
							size++;
						}
						
						progressBar.setValue(0);
						progressBar.setMaximum(size*AnzahlRegion*(titel.length -2));
						UpdateUI();
					}
				});
				t.start();
				
			}
		});
		pushbutton.setText("OK");
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label);
		panel.add(slider);
		panel.add(pushbutton);
		add(panel,BorderLayout.WEST);
		
		
		
		
		// Das JTable initialisieren
		table = new JTable( data, titel);
		
		add(new JScrollPane(table),BorderLayout.CENTER);
		this.setSize(800, 600);
		mgr = new DB2ConnectionManager();
	}
	
	
	
	
	
}
