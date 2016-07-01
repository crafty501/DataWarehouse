package kolja.query;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
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
	long maxdays;
	int size;
	int period;
	SimpleDateFormat dateFormat;
	JLabel label;
	String Anfrage1 = "SELECT REGION FROM SHOP GROUP BY REGION";
	
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
	
	
	private void CreateData(boolean update){
		
		assert size > 0 : "Vorbedingung verletzt! size muss grösser null sein";
			
		try{	
			
		ResultSet res = mgr.SendQuery(Anfrage1, true);
		int zeile = 0;
		int AnzahlRegion = 0;
		
		while (res.next()) {
			AnzahlRegion++;
		}
		System.out.println("size= "+size);
		System.out.println("AnzahlRegion= "+AnzahlRegion);
		data = new String[(int) ((size)*AnzahlRegion)+100][titel.length];
		res = mgr.SendQuery(Anfrage1, true);
		while(res.next()){
			
			String startDate = "01.01.2015";
			String Region = res.getString(1);
			for(int p = 0; p < maxdays ; p = p + period ){
				data[zeile][0]= Region; 
				Date date;
				try {
					date = dateFormat.parse(startDate);
					long time = date.getTime() + (long)(1000 * 60 * 60 * 24 * (long)period) ;
					Date plusdays = new Date(time);
					String endDate = dateFormat.format(plusdays);
					//System.out.println(startDate+"  -  "+endDate);
					for (int i = 2 ; i < titel.length ; i++){
						String Product = titel[i];
						QueryThread t = new QueryThread();
						t.SetParameter(mgr,Region,Product,startDate,endDate,zeile,i);
						t.SetUIParam(table);
						t.SetData(data);
						t.SetUpdate(update);
						t.start();
						System.out.println("Thread erstellt");
					}
					
				zeile++;
				startDate = endDate;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		
		}
		} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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
	table.repaint();
	
	
	}
	
	
public uitabelle(DB2ConnectionManager m){
		super();

		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		this.mgr = m;
		
		maxdays = this.GetDays();
		size =(int)(maxdays / 149);
		period = 149;
		CreateTitel();
		CreateData(false);
		
		
		this.setLayout(new FlowLayout());
		
		
		
		
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
		
		add(label);
		
		
		
		add(slider);
		
		
		
		JButton pushbutton = new JButton();
		pushbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				period = slider.getValue();
				
				size = (int)Math.round( (float)maxdays / (float)period);
				
				
				System.out.println("Days:"+maxdays);
				System.out.println("Size:"+size);
				
				UpdateUI();
				
			}
		});
		pushbutton.setText("OK");
		add(pushbutton);
		// Das JTable initialisieren
		table = new JTable( data, titel);
		
		add(new JScrollPane(table));
		this.setSize(800, 600);
		
	}
	
	
	
	
	
}
