package org.pguia.java.swing.jdbc;

import org.pguia.java.swing.jdbc.model.Product;
import org.pguia.java.swing.jdbc.repository.IProductRepository;
import org.pguia.java.swing.jdbc.repository.ProductRepositoryImpl;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductsMaintenance extends JFrame {
    private Container p;
    private IProductRepository productRepository;
    private ProductTableModel productTableModel;
    private JTable jTable;

    public JdbcProductsMaintenance() {
        setTitle("Products Maintenance");
        setLocationRelativeTo(null);

        p = getContentPane();
        p.setLayout(new BorderLayout(20, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(200, 0 ,0));

        JLabel title = new JLabel("Mantenimiento de Productos");
        title.setFont(new Font("Times New Roman", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setOpaque(true);
        title.setBackground(new Color(52, 152, 219));

        productRepository = new ProductRepositoryImpl();
        productTableModel = new ProductTableModel();
        jTable = new JTable(productTableModel);

        JButton buttonAgregar = crearButton("Agregar");
        JButton buttonModificar = crearButton("Modificar");
        JButton buttonEliminar = crearButton("Eliminar");
        JButton buttonConsultar = crearButton("Consultar");
        JButton buttonSalir = crearButton("Salir");

        buttonAgregar.addActionListener(e -> agregarProductos());
        buttonModificar.addActionListener(e -> modificarProducto());
        buttonEliminar.addActionListener(e -> eliminarProducto());
        buttonConsultar.addActionListener(e -> consultarProducto());
        buttonSalir.addActionListener(e -> System.exit(0));

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(new Color(200, 0 ,0));

        panelBotones.add(buttonAgregar);
        panelBotones.add(buttonModificar);
        panelBotones.add(buttonEliminar);
        panelBotones.add(buttonConsultar);
        panelBotones.add(buttonSalir);

        JPanel tablePanel = new JPanel(new FlowLayout());
        JScrollPane scroll = new JScrollPane(jTable);
        tablePanel.add(scroll);

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(panelBotones);

        p.add(topPanel, BorderLayout.NORTH);
        p.add(tablePanel, BorderLayout.SOUTH);

        setContentPane(p);
        setSize(700, 600);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JButton crearButton(String texto) {
        JButton button = new JButton(texto);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(46, 204, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        return button;
    }

    public static void main(String[] args) {
        new JdbcProductsMaintenance();
    }

    private void agregarProductos() {
        JDialog dialog = new JDialog(this, "Agregar Productos", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(4, 2));

        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);

        dialog.add(new JLabel("Nombre: "));
        dialog.add(nameField);
        dialog.add(new JLabel("Precio: "));
        dialog.add(priceField);
        dialog.add(new JLabel("Cantidad: "));
        dialog.add(quantityField);

        JButton buttonGuardar = new JButton("Guardar");
        buttonGuardar.addActionListener(e -> {
            String name = nameField.getText();
            int price = 0;
            int quantity = 0;
            try {
                price = Integer.parseInt(priceField.getText());
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException numberFormatException) {}

            List<String> errors = new ArrayList<>();
            if(name.isBlank()) {
                errors.add("Debe ingresar el nombre");
            }

            if(price == 0) {
                errors.add("El precio es requerido y numérico");
            }

            if(quantity == 0) {
                errors.add("La cantidad no debe ser cero");
            }

            productRepository.save(new Product(null, name, price, quantity));
            dialog.dispose();
        });
        JOptionPane.showMessageDialog(this, "Producto agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dialog.add(buttonGuardar);
        dialog.setVisible(true);
    }

    private void modificarProducto() {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para modificar",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos del producto seleccionado
        Long id = (Long) productTableModel.getValueAt(selectedRow, 0);
        String name = (String) productTableModel.getValueAt(selectedRow, 1);
        int price = (int) productTableModel.getValueAt(selectedRow, 2);
        int quantity = (int) productTableModel.getValueAt(selectedRow, 3);

        // Confirmación antes de modificar
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Deseas modificar este producto?",
                "Confirmed", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        // Crear cuadro de diálogo para modificar
        JDialog dialog = new JDialog(this, "Modificar Producto", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(4, 2));

        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);

        dialog.add(new JLabel("Nombre: "));
        dialog.add(nameField);
        dialog.add(new JLabel("Precio: "));
        dialog.add(priceField);
        dialog.add(new JLabel("Cantidad: "));
        dialog.add(quantityField);

        JButton buttonActualizar = new JButton("Actualizar");
        buttonActualizar.addActionListener(e -> {
            String newName = nameField.getText();
            int newPrice = 0;
            int newQuantity = 0;
            try {
                newPrice = Integer.parseInt(priceField.getText());
                newQuantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(dialog, "Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newName.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Debe ingresar el nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Actualizar producto de base de datos
            productRepository.save(new Product(id, newName, newPrice, newQuantity));

            //Reflejar cambios en la tabla
            productTableModel.setValueAt(newName, selectedRow, 1);
            productTableModel.setValueAt(newPrice, selectedRow, 2);
            productTableModel.setValueAt(newQuantity, selectedRow, 3);
            productTableModel.fireTableDataChanged();
            JOptionPane.showMessageDialog(this, "Producto modificado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        dialog.add(buttonActualizar);
        dialog.setVisible(true);
    }

    private void eliminarProducto() {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para eliminar",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Obtener el ID del producto seleccionado
        Long id = (Long) productTableModel.getValueAt(selectedRow, 0);
        // Confirmar antes de eliminar
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar este producto?",
                "Confirmed", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        // Eliminar producto de la base de datos y actualizar la tabla
        productRepository.delete(id);
        productTableModel.getRows().remove(selectedRow);
        productTableModel.fireTableDataChanged();
        JOptionPane.showMessageDialog(this, "Producto eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void consultarProducto() {
        int selectedRow = jTable.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para la consulta",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos del producto seleccionado
        Long id = (Long) productTableModel.getValueAt(selectedRow, 0);
        String name = (String) productTableModel.getValueAt(selectedRow, 1);
        int price = (int) productTableModel.getValueAt(selectedRow, 2);
        int quantity = (int) productTableModel.getValueAt(selectedRow, 3);

        // Crear un panel para mostrar la información
        JPanel panelInformation = new JPanel(new GridLayout(4, 2));

        panelInformation.add(new JLabel("ID: "));
        panelInformation.add(new JLabel(String.valueOf(id)));
        panelInformation.add(new JLabel("Nombre: "));
        panelInformation.add(new JLabel(String.valueOf(name)));
        panelInformation.add(new JLabel("Precio: "));
        panelInformation.add(new JLabel(String.valueOf(price)));
        panelInformation.add(new JLabel("Cantidad: "));
        panelInformation.add(new JLabel(String.valueOf(quantity)));

        // Crear un cuadro de diálogo para mostrar la información
        JDialog dialog = new JDialog(this, "Información de Producto", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.add(panelInformation, BorderLayout.CENTER);

        // Botón para cerrar
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private class ProductTableModel extends AbstractTableModel {

        private String[] columns = new String[]{"Id", "Nombre", "Precio", "Cantidad"};
        private java.util.List<Object[]> rows = new ArrayList<>();

        public ProductTableModel() {
            IProductRepository productRepository = new ProductRepositoryImpl();
            java.util.List<Product> products = productRepository.findAll();
            for(Product product: products) {
                Object[] row = {product.getId(), product.getName(), product.getPrice(), product.getQuantity()};
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
