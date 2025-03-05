package org.pguia.java.swing.jdbc;


import org.pguia.java.swing.jdbc.model.Product;
import org.pguia.java.swing.jdbc.repository.IProductRepository;
import org.pguia.java.swing.jdbc.repository.ProductRepositoryImpl;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class JdbcSwingCrudProject extends JFrame {

    private Container p;
    private JTextField nameField = new JTextField();
    private JTextField priceField = new JTextField();
    private JTextField quantityField = new JTextField();
    private ProductTableModel tableModel = new ProductTableModel();
    private IProductRepository productRepository;

    private long id;
    private int row;

    public JdbcSwingCrudProject() {
        super("Swing: GUI con Base de Datos MySQL!");
        p = getContentPane();
        p.setLayout(new BorderLayout(20, 10));

         productRepository = new ProductRepositoryImpl();
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 20, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton buttonSave = new JButton("Guardar");

        formPanel.add(new JLabel("Nombre: "));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Precio: "));
        formPanel.add(priceField);

        formPanel.add(new JLabel("Cantidad: "));
        formPanel.add(quantityField);

        formPanel.add(new JLabel(""));
        formPanel.add(buttonSave);
        buttonSave.addActionListener(e -> {
            String name = nameField.getText();
            int price = 0;
            int quantity = 0;
            try {
                price = Integer.parseInt(priceField.getText());
            } catch (NumberFormatException numberFormatException) {}

            try {
                quantity = Integer.parseInt(quantityField.getText());
            }  catch (NumberFormatException numberFormatException) {}

            List<String> errors = new ArrayList<>();
            if(name.isBlank()) {
                errors.add("Debe ingresar el nombre");
            }

            if(price == 0) {
                errors.add("El precio es requerido y numérico");
            }

            if(quantity == 0) {
                errors.add("La cantidad no debe ser cero y numérica");
            }

            if(errors.size() > 0) {
                JOptionPane.showMessageDialog(null, errors.toArray(),
                        "Error en la validación", JOptionPane.ERROR_MESSAGE);
            } else {
                Product productDb = productRepository.save(new Product(id == 0?null : id, name, price, quantity));
                if (id == 0) {
                    Object[] product = new Object[]{productDb.getId(), name, price, quantity, "remove"};
                    tableModel.getRows().add(product);
                    tableModel.fireTableDataChanged();

                    System.out.println(product[0]);
                    System.out.println(product[1]);
                    System.out.println(product[2]);
                } else if (id > 0) {
                    tableModel.setValueAt(id, row, 0);
                    tableModel.setValueAt(name, row, 1);
                    tableModel.setValueAt(price, row, 2);
                    tableModel.setValueAt(quantity, row, 3);
                }
                reset();
            }
        });

        JPanel tablePanel = new JPanel(new FlowLayout());

        JTable jTable = new JTable();
        jTable.setModel(this.tableModel);
        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                row = jTable.rowAtPoint(e.getPoint());
                int column = jTable.columnAtPoint(e.getPoint());

                if(row > -1 && column == 4) {
                    int option = JOptionPane.showConfirmDialog(null, "Esta seguro que desea eliminar el registro " +
                                    tableModel.getValueAt(row, 1).toString() +
                            "?", "Cuidado Eliminar Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if(option == JOptionPane.OK_OPTION) {
                        productRepository.delete((long) tableModel.getValueAt(row, 0));
                        tableModel.getRows().remove(row);
                        tableModel.fireTableDataChanged();
                    }
                    reset();
                } else if(row > -1 && column > -1) {
                    id = (long) tableModel.getValueAt(row, 0);
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    priceField.setText(tableModel.getValueAt(row, 2).toString());
                    quantityField.setText(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(jTable);
        tablePanel.add(scroll);
        p.add(tablePanel, BorderLayout.SOUTH);
        p.add(formPanel, BorderLayout.NORTH);
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void reset() {
        id = 0;
        row = -1;
        nameField.setText("");
        priceField.setText("");
        quantityField.setText("");
    }

    public static void main(String[] args) {
        new JdbcSwingCrudProject();
    }

    private class ProductTableModel extends AbstractTableModel {

        private String[] columns = new String[]{"Id", "Nombre", "Precio", "Cantidad", "Delete"};
        private List<Object[]> rows = new ArrayList<>();

        public ProductTableModel() {
            IProductRepository productRepository = new ProductRepositoryImpl();
            List<Product> products = productRepository.findAll();
            for(Product product: products) {
                Object[] row = {product.getId(), product.getName(), product.getPrice(), product.getQuantity(), "remove"};
                rows.add(row);
            }
        }

        public List<Object[]> getRows() {
            return rows;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return rows.get(rowIndex)[columnIndex];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            rows.get(rowIndex)[columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }
    }
}
