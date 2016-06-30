package kolja.query;

import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
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
	
	private ResultSet MakeQuery(String Region,String Product,String StartDate ,String EndDate){
		
		String Anfrage = "SELECT sum(VERKAUFT) "
				+ "FROM SHOP S, Artikel A, SALES SA "
				+ "WHERE "
				+ "A.NAME=SA.ARTIKEL "
				+ "AND S.NAME=SA.SHOP "
				+ "AND A.NAME='"+Product+"' "
				+ "AND REGION='"+Region+"' "
				+ "AND DATUM > '"+StartDate+"' "
				+ "AND DATUM < '"+EndDate+"'";
		//System.out.println(Anfrage);
		try {
			return mgr.SendQuery(Anfrage, true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
		String Anfrage = "SELECT REGION FROM SHOP GROUP BY REGION";	
		ResultSet res = mgr.SendQuery(Anfrage, true);
		int zeile = 0;
		int AnzahlRegion = 0;
		
		while (res.next()) {
			AnzahlRegion++;
		}
		System.out.println("size= "+size);
		data = new String[(int) (maxdays*AnzahlRegion)][titel.length];
		 res = mgr.SendQuery(Anfrage, true);
		while(res.next()){
			
			String startDate = "01.01.2015";
			String Region = res.getString(1);
			for(int p = 0; p < maxdays ; p = p + period ){
				
				data[zeile][0]= Region; 
				System.out.println(Region);
				Date date;
				try {
					date = dateFormat.parse(startDate);
					//System.out.println("period"+period);
					long time = date.getTime() + (long)(1000 * 60 * 60 * 24 * (long)period) ;
					Date plusdays = new Date(time);
					//System.out.println(plusdays.toString());
					String endDate = dateFormat.format(plusdays);
					if (update){
						table.setValueAt(startDate + " - " + endDate, zeile,1);
					}else{
						data[zeile][1]=startDate + " - " + endDate;
					}
					System.out.println(startDate+"  -  "+endDate);
					for (int i = 2 ; i < titel.length ; i++){
						String Product = titel[i];
						ResultSet r = MakeQuery(Region,Product,startDate,endDate);
						r.next();
						if(update){
							table.setValueAt(r.getString(1), zeile, i);
						}else{
							data[zeile][i] = r.getString(1);
						}
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
				
				
				period = slider.getValue();
				
				size =(int)(maxdays / period);
				System.out.println("Days:"+maxdays);
				System.out.println("Size:"+size);
				
				UpdateUI();
				
				
			}
		});
		add(slider);
		
		// Das JTable initialisieren
		table = new JTable( data, titel);
		
		add(new JScrollPane(table));
		this.setSize(800, 600);
		
	}
	
	
	
	
	
}
