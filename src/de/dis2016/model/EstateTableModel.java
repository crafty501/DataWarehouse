package de.dis2016.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class EstateTableModel extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	private List<Estate> estates;
	
	public EstateTableModel(List<Estate> estates) {
		this.estates = estates;
	}
	
	
	@Override
	public int getColumnCount() {
		return 6;
	}

	@Override
	public int getRowCount() {
		return estates.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = "??";
        Estate estate = estates.get(rowIndex);
        switch (columnIndex) {
            case 0:
                value = estate.getId();
                break;
            case 1:
                value = estate.getCity();
                break;
            case 2:
                value = estate.getPostalCode();
                break;
            case 3:
                value = estate.getStreet();
                break;
            case 4:
                value = estate.getStreetNr();
                break;
            case 5:
                value = estate.getSquareArea();
                break;
        }

        return value;
	}
	
	public Estate getEstateAt(int row) {
        return estates.get(row);
    }

}
