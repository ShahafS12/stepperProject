package mta.course.java.stepper.dd.impl.relation;

import java.util.ArrayList;

public class RelationData {

    private ArrayList<String> columns;
    private ArrayList<SingleRow> rows;
    public RelationData(ArrayList<String> columns) {
        this.columns = columns;
        rows = new ArrayList<>();
    }

    public RelationData() {
        columns = new ArrayList<>();
        rows = new ArrayList<>();
    }
    public boolean isEmpty() {
        return columns.isEmpty() && rows.isEmpty();
    }

    public void printTable(){
        System.out.println(this.getColumns().toString());
        for (RelationData.SingleRow row : this.getRows()) {
            System.out.println(row.getData().toString());
        }

    }
    public String getValInList(int index){return columns.get(index);}
    public ArrayList<String> getRowDataByColumnsOrder(int rowId) {return new ArrayList<>();}
    public int getNumColumns() {return columns.size();}
    public int getNumRows(){return rows.size();}
    public String getValueAt(int index1, int index2) {
        SingleRow t = rows.get(index1);
        return t.getData().get(index2);
    }
    public ArrayList<String> getColumns() {return columns;}

    public void setColumns(ArrayList<String> columns) {this.columns = columns;}

    public ArrayList<SingleRow> getRows() {return rows;}

    public void setRows(ArrayList<SingleRow> rows) {this.rows = rows;}

    public void addRow( ArrayList<String> row) {
        SingleRow temp = new SingleRow();
        for (int i = 0; i < row.size(); i++) {
            temp.addData(i, row.get(i));
        }
        rows.add(temp);
    }
    //////////////////////////////
    public static class SingleRow {
        private ArrayList<String> data;
        public ArrayList<String> getData() {return data;}

        public void setData(ArrayList<String> data) {this.data = data;}

        public SingleRow() {
            data = new ArrayList<>();
        }
        public void addData(int columnNum, String value) {
            data.add(columnNum, value);
        }
        // todo=> add a new line, add a new row
    }
}