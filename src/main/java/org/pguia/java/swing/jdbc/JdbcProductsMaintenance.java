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
    private final IProductRepository productRepository;
    private final ProductTableModel productTableModel;
    private final JTable jTable;

    public JdbcProductsMaintenance() {
        setTitle("Products Maintenance");
        setLocationRelativeTo(null);

        p = getContentPane();
        p.setLayout(new BorderLayout(20, 10));

        // Incluimos una imagen para el icono del programa
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
        setIconImage(imageIcon.getImage());

        // El panel superior del programa
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(200, 0 ,0));

        // Título para la pantalla
        JLabel title = new JLabel("Mantenimiento de Productos");
        title.setFont(new Font("Times New Roman", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setOpaque(true);
        title.setBackground(new Color(52, 152, 219));

        productRepository = new ProductRepositoryImpl();
        productTableModel = new ProductTableModel();
        jTable = new JTable(productTableModel);

        // Ajustar el tamaño de la tabla
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable.setFillsViewportHeight(true);

        int[] columnsWidths = {50, 150, 80, 80, 120, 120, 100, 200};
        for (int i = 0; i < jTable.getColumnCount(); i++) {
            jTable.getColumnModel().getColumn(i).setPreferredWidth(columnsWidths[i]);
        }

        // Los botones con sus respectivas interacciones.
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

        // El panel de controles
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
        scroll.setPreferredSize(new Dimension(700, 400));
        tablePanel.add(scroll);

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(title);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(panelBotones);

        p.add(topPanel, BorderLayout.NORTH);
        p.add(tablePanel, BorderLayout.SOUTH);

        setContentPane(p);
        setSize(800, 600);
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
        dialog.setLayout(new GridLayout(8, 2, 5, 5));

        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        JTextField supplierField = new JTextField(20);
        JTextField statusField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);

        dialog.add(new JLabel("Nombre: "));
        dialog.add(nameField);
        dialog.add(new JLabel("Precio: "));
        dialog.add(priceField);
        dialog.add(new JLabel("Cantidad: "));
        dialog.add(quantityField);
        dialog.add(new JLabel("Categoria: "));
        dialog.add(categoryField);
        dialog.add(new JLabel("Proveedor: "));
        dialog.add(supplierField);
        dialog.add(new JLabel("Estado: "));
        dialog.add(statusField);
        dialog.add(new JLabel("Descripcion: "));
        dialog.add(descriptionField);

        JButton buttonGuardar = new JButton("Guardar");
        buttonGuardar.addActionListener(e -> {
            String name = nameField.getText();
            int price = 0;
            int quantity = 0;
            String category = categoryField.getText().trim();
            String supplier = supplierField.getText().trim();
            String status = statusField.getText().trim();
            String description = descriptionField.getText().trim();
            try {
                price = Integer.parseInt(priceField.getText().trim());
                quantity = Integer.parseInt(quantityField.getText().trim());
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

            if(category.isBlank()) {
                errors.add("Debe ingresar la categoría");
            }

            if(supplier.isBlank()) {
                errors.add("Debe ingresar la proveedor");
            }

            if(status.isBlank()) {
                errors.add("Debe ingresar el estado");
            }

            if(description.isBlank()) {
                errors.add("Debe ingresar la descripción");
            }

            Product newProduct = new Product(null, name, price, quantity, category, supplier, status, description);
            productRepository.save(newProduct);
            actualizarTablaProductos(); // Recargar los datos de la BD y actualizar la tabla
            productTableModel.fireTableDataChanged(); // Forzar actualización de la tabla
            JOptionPane.showMessageDialog(this, "Producto agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        dialog.add(new JLabel()); // Espacio vacío para alineación
        dialog.add(buttonGuardar);

        dialog.pack(); // Ajustar el tamaño automáticamente al contenido
        dialog.setLocationRelativeTo(this); // centrar la pantalla
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
        String category = (String) productTableModel.getValueAt(selectedRow, 4);
        String supplier = (String) productTableModel.getValueAt(selectedRow, 5);
        String status = (String) productTableModel.getValueAt(selectedRow, 6);
        String description = (String) productTableModel.getValueAt(selectedRow, 7);

        // Confirmación antes de modificar
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Deseas modificar este producto?",
                "Confirmed", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        // Crear cuadro de diálogo para modificar
        JDialog dialog = new JDialog(this, "Modificar Producto", true);
        dialog.setLayout(new GridLayout(8, 2, 5, 5));

        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        JTextField supplierField = new JTextField(20);
        JTextField statusField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);

        dialog.add(new JLabel("Nombre: "));
        dialog.add(nameField);
        dialog.add(new JLabel("Precio: "));
        dialog.add(priceField);
        dialog.add(new JLabel("Cantidad: "));
        dialog.add(quantityField);
        dialog.add(new JLabel("Categoria: "));
        dialog.add(categoryField);
        dialog.add(new JLabel("Proveedor: "));
        dialog.add(supplierField);
        dialog.add(new JLabel("Estado: "));
        dialog.add(statusField);
        dialog.add(new JLabel("Descripcion: "));
        dialog.add(descriptionField);

        JButton buttonActualizar = new JButton("Actualizar");
        buttonActualizar.addActionListener(e -> {
            String newName = nameField.getText();
            int newPrice = 0;
            int newQuantity = 0;
            String newCategory = categoryField.getText();
            String newSupplier = supplierField.getText();
            String newStatus = statusField.getText();
            String newDescription= descriptionField.getText();
            try {
                newPrice = Integer.parseInt(priceField.getText());
                newQuantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException numberFormatException) {
                JOptionPane.showMessageDialog(dialog, "Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newName.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Debe ingresar el nombre", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newCategory.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Debe ingresar la categoría", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newSupplier.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Debe ingresar el proveedor", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newStatus.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Debe ingresar el estado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newDescription.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "Debe ingresar la descripción", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Actualizar producto de base de datos
            productRepository.save(new Product(id, newName, newPrice, newQuantity, newCategory, newSupplier, newStatus, newDescription));

            //Reflejar cambios en la tabla
            productTableModel.setValueAt(newName, selectedRow, 1);
            productTableModel.setValueAt(newPrice, selectedRow, 2);
            productTableModel.setValueAt(newQuantity, selectedRow, 3);
            productTableModel.setValueAt(newCategory, selectedRow, 4);
            productTableModel.setValueAt(newSupplier, selectedRow, 5);
            productTableModel.setValueAt(newStatus, selectedRow, 6);
            productTableModel.setValueAt(newDescription, selectedRow, 7);
            productTableModel.fireTableDataChanged();
            JOptionPane.showMessageDialog(this, "Producto modificado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        dialog.add(buttonActualizar);
        dialog.pack();
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
        String category = (String) productTableModel.getValueAt(selectedRow, 4);
        String supplier = (String) productTableModel.getValueAt(selectedRow, 5);
        String status = (String) productTableModel.getValueAt(selectedRow, 6);
        String description = (String) productTableModel.getValueAt(selectedRow, 7);

        // Crear un panel para mostrar la información
        JPanel panelInformation = new JPanel(new GridLayout(8, 2));

        panelInformation.add(new JLabel("ID: "));
        panelInformation.add(new JLabel(String.valueOf(id)));
        panelInformation.add(new JLabel("Nombre: "));
        panelInformation.add(new JLabel(String.valueOf(name)));
        panelInformation.add(new JLabel("Precio: "));
        panelInformation.add(new JLabel(String.valueOf(price)));
        panelInformation.add(new JLabel("Cantidad: "));
        panelInformation.add(new JLabel(String.valueOf(quantity)));
        panelInformation.add(new JLabel("Categoria: "));
        panelInformation.add(new JLabel(String.valueOf(category)));
        panelInformation.add(new JLabel("Proveedor: "));
        panelInformation.add(new JLabel(String.valueOf(supplier)));
        panelInformation.add(new JLabel("Estado: "));
        panelInformation.add(new JLabel(String.valueOf(status)));
        panelInformation.add(new JLabel("Descripción: "));
        panelInformation.add(new JLabel(String.valueOf(description)));

        // Crear un cuadro de diálogo para mostrar la información
        JDialog dialog = new JDialog(this, "Información de Producto", true);
        dialog.setLayout(new BorderLayout(5, 5));
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

    private void actualizarTablaProductos() {
        List<Product> productosActualizados = productRepository.findAll(); // Recargar de la base de datos
        productTableModel.setProductos(productosActualizados); // Actualizar el modelo con los nuevos datos
        productTableModel.fireTableDataChanged(); // Refrescar la tabla
    }

    private class ProductTableModel extends AbstractTableModel {

        private String[] columns = new String[]{"Id", "Nombre", "Precio", "Cantidad", "Categoria", "Proveedor", "Estado", "Descripcion"};
        private List<Object[]> rows = new ArrayList<>();
        List<Product> products;

        public ProductTableModel() {
            IProductRepository productRepository = new ProductRepositoryImpl();
            products = productRepository.findAll();
            for(Product product: products) {
                Object[] row = {product.getId(), product.getName(), product.getPrice(), product.getQuantity(), product.getCategory(), product.getSupplier(),
                                product.getStatus(), product.getDescription()};
                rows.add(row);
            }
        }

        public void setProductos(List<Product> productos) {
            this.products = productos;
            rows.clear();
            for(Product product: products) {
                Object[] row = {product.getId(), product.getName(), product.getPrice(), product.getQuantity(), product.getCategory(), product.getSupplier(),
                        product.getStatus(), product.getDescription()};
                rows.add(row);
            }
            fireTableDataChanged(); // Notificar actualización de la tabla
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
