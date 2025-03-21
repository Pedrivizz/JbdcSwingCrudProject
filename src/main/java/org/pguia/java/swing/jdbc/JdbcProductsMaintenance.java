package org.pguia.java.swing.jdbc;

import org.pguia.java.swing.jdbc.model.Product;
import org.pguia.java.swing.jdbc.repository.IProductRepository;
import org.pguia.java.swing.jdbc.repository.ProductRepositoryImpl;

// Java Swing and MySQL Connection Dependencies
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        int columnCount = jTable.getColumnCount();
        int widthCount = Math.min(columnsWidths.length, columnCount);

        for (int i = 0; i < widthCount; i++) {
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
        //panelBotones.add(buttonReporte);
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
        dialog.setSize(450, 500);
        dialog.setLayout(new BorderLayout());
        // dialog.setLayout(new GridLayout(10, 2, 5, 5));

        // Panel personalizado para separador
        JPanel panelForm = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(10, getHeight() - 40, getWidth() - 10, 40);
            }
        };
        panelForm.setLayout(null);

        // Posiciones y dimensiones
        int xLabel = 20, xField = 120, y = 20, widthLabel = 90, widthField = 200, height = 25;

        JLabel nameLabel = new JLabel("Nombre: ");
        nameLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField nameField = new JTextField(20);
        nameField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel priceLabel = new JLabel("Precio: ");
        priceLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField priceField = new JTextField(20);
        priceField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel quantityLabel = new JLabel("Cantidad: ");
        quantityLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField quantityField = new JTextField(20);
        quantityField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel categoryLabel = new JLabel("Categoria: ");
        categoryLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField categoryField = new JTextField(20);
        categoryField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel supplierLabel = new JLabel("Proveedor: ");
        supplierLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField supplierField = new JTextField(20);
        supplierField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel statusLabel = new JLabel("Estado: ");
        statusLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField statusField = new JTextField(20);
        statusField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel descriptionLabel = new JLabel("Descripción: ");
        descriptionLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField descriptionField = new JTextField(20);
        descriptionField.setBounds(xField, y, widthField, height);

        /// Creamos botón para seleccionar la imagen del producto
        y += 35;
        JLabel imageLabel = new JLabel("Imagen: ");
        imageLabel.setBounds(xLabel, y, widthLabel, height);
        JButton buttonImage = new JButton("Seleccionar Imagen");
        buttonImage.setBounds(xField, y, widthField, height);

        JLabel labelImage = new JLabel("Ninguna imagen seleccionada");
        labelImage.setBounds(xField, y, widthField, height);

        // Para almacenar la imagen seleccionada
        final File[] selectedFile = {null};

        buttonImage.addActionListener(i -> {
            JFileChooser fileChooser = new JFileChooser();
            int rs = fileChooser.showOpenDialog(dialog);

            if(rs == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                labelImage.setText(selectedFile[0].getName());
            }
        });

        y += 60;
        JButton buttonGuardar = new JButton("Guardar");
        buttonGuardar.setBounds((dialog.getWidth() - 120) / 2, y, 120, height);

        buttonGuardar.addActionListener(e -> {
            String name = nameField.getText();
            int price = 0;
            int quantity = 0;
            String category = categoryField.getText().trim();
            String supplier = supplierField.getText().trim();
            String status = statusField.getText().trim();
            String description = descriptionField.getText().trim();
            byte[] imageBytes = null;

            if (selectedFile[0] != null) {
                try {
                    imageBytes = Files.readAllBytes(selectedFile[0].toPath()); // Convertir imagen a bytes
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error al leer la imagen: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Salir del método si hay un error al leer la imagen.
                }
            }

            try {
                price = Integer.parseInt(priceField.getText().trim());
                quantity = Integer.parseInt(quantityField.getText().trim());
            } catch (NumberFormatException numberFormatException) {}

            List<String> errors = new ArrayList<>();
            if(name.isBlank()) errors.add("Debe ingresar el nombre");
            if(price == 0) errors.add("El precio es requerido y numérico");
            if(quantity == 0) errors.add("La cantidad no debe ser cero");
            if(category.isBlank()) errors.add("Debe ingresar la categoría");
            if(supplier.isBlank()) errors.add("Debe ingresar la proveedor");
            if(status.isBlank()) errors.add("Debe ingresar el estado");
            if(description.isBlank()) errors.add("Debe ingresar la descripción");

            if(!errors.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, String.join("\n", errors),
                        "Errores", JOptionPane.ERROR_MESSAGE);
                return;
            }


            Product newProduct = new Product(null, name, price, quantity, category, supplier, status, description, imageBytes);
            productRepository.save(newProduct);
            actualizarTablaProductos(); // Recargar los datos de la BD y actualizar la tabla
            productTableModel.fireTableDataChanged(); // Forzar actualización de la tabla
            JOptionPane.showMessageDialog(this, "Producto agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        // Agregar componentes
        panelForm.add(nameLabel);
        panelForm.add(nameField);
        panelForm.add(priceLabel);
        panelForm.add(priceField);
        panelForm.add(quantityLabel);
        panelForm.add(quantityField);
        panelForm.add(categoryLabel);
        panelForm.add(categoryField);
        panelForm.add(supplierLabel);
        panelForm.add(supplierField);
        panelForm.add(statusLabel);
        panelForm.add(statusField);
        panelForm.add(descriptionLabel);
        panelForm.add(descriptionField);
        panelForm.add(imageLabel);
        panelForm.add(buttonImage);
        panelForm.add(labelImage);
        panelForm.add(buttonGuardar);

        dialog.add(panelForm, BorderLayout.CENTER);
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

        // Validar que la tabla tenga suficientes columnas
        if (productTableModel.getColumnCount() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Error: La tabla no tiene el número esperado de columnas.",
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
        AtomicReference<byte[]> imageData = new AtomicReference<>(null);

        // Confirmación antes de modificar
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Deseas modificar este producto?",
                "Confirmed", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        // Crear cuadro de diálogo para modificar
        JDialog dialog = new JDialog(this, "Modificar Producto", true);
        dialog.setSize(450, 500);
        dialog.setLayout(new BorderLayout());

        JPanel panelForm = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(10, getHeight() - 40, getWidth() - 10, 40);
            }
        };
        panelForm.setLayout(null);
        // Posiciones y dimensiones fijas
        int xLabel = 20, xField = 120, y = 20, widthLabel = 90, widthField = 200, height = 25;

        // Crear cuadro de diálogo para modificar
        JLabel nameLabel = new JLabel("Nombre: ");
        nameLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField nameField = new JTextField(name, 20);
        nameField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel priceLabel = new JLabel("Precio: ");
        priceLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField priceField = new JTextField(String.valueOf(price),20);
        priceField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel quantityLabel = new JLabel("Cantidad: ");
        quantityLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField quantityField = new JTextField(String.valueOf(quantity),20);
        quantityField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel categoryLabel = new JLabel("Cateogoria: ");
        categoryLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField categoryField = new JTextField(category,20);
        categoryField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel supplierLabel = new JLabel("Proveedor:");
        supplierLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField supplierField = new JTextField(supplier,20);
        supplierField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel statusLabel = new JLabel("Estado:");
        statusLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField statusField = new JTextField(status,20);
        statusField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel descriptionLabel = new JLabel("Descripción: ");
        descriptionLabel.setBounds(xLabel, y, widthLabel, height);
        JTextField descriptionField = new JTextField(description,20);
        descriptionField.setBounds(xField, y, widthField, height);

        y += 35;
        JLabel imageLabel = new JLabel("Imagen: ");
        imageLabel.setBounds(xLabel, y, widthLabel, height);
        JButton buttonImage = new JButton("Seleccionar Imagen");
        buttonImage.setBounds(xField, y, widthField, height);

        JLabel labelImage = new JLabel("Ninguna imagen seleccionada");
        labelImage.setBounds(xField, y + 30, widthField, height);

        buttonImage.addActionListener(i -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));
            int rs = fileChooser.showOpenDialog(dialog);

            if(rs == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                labelImage.setText(selectedFile.getName());

                try {
                    imageData.set(Files.readAllBytes(selectedFile.toPath())); // Actualizar la imagen
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al cargar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        y += 60;
        JButton buttonActualizar = new JButton("Actualizar");
        buttonActualizar.setBounds(xField, y, widthField, height);

        buttonActualizar.addActionListener(e -> {
            String newName = nameField.getText().trim();
            int newPrice = 0;
            int newQuantity = 0;
            String newCategory = categoryField.getText().trim();
            String newSupplier = supplierField.getText().trim();
            String newStatus = statusField.getText().trim();
            String newDescription= descriptionField.getText().trim();
            try {
                newPrice = Integer.parseInt(priceField.getText().trim());
                newQuantity = Integer.parseInt(quantityField.getText().trim());
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
            productRepository.save(new Product(id, newName, newPrice, newQuantity, newCategory, newSupplier, newStatus, newDescription, imageData.get()));
            actualizarTablaProductos(); 

            //Reflejar cambios en la tabla
            if(selectedRow < productTableModel.getRowCount()) {
                productTableModel.setValueAt(newName, selectedRow, 1);
                productTableModel.setValueAt(newPrice, selectedRow, 2);
                productTableModel.setValueAt(newQuantity, selectedRow, 3);
                productTableModel.setValueAt(newCategory, selectedRow, 4);
                productTableModel.setValueAt(newSupplier, selectedRow, 5);
                productTableModel.setValueAt(newStatus, selectedRow, 6);
                productTableModel.setValueAt(newDescription, selectedRow, 7);
                productTableModel.fireTableDataChanged();
            }
            JOptionPane.showMessageDialog(this, "Producto modificado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        panelForm.add(nameLabel);
        panelForm.add(nameField);
        panelForm.add(priceLabel);
        panelForm.add(priceField);
        panelForm.add(quantityLabel);
        panelForm.add(quantityField);
        panelForm.add(categoryLabel);
        panelForm.add(categoryField);
        panelForm.add(supplierLabel);
        panelForm.add(supplierField);
        panelForm.add(statusLabel);
        panelForm.add(statusField);
        panelForm.add(descriptionLabel);
        panelForm.add(descriptionField);
        panelForm.add(imageLabel);
        panelForm.add(buttonImage);
        panelForm.add(labelImage);
        panelForm.add(buttonActualizar);

        dialog.add(panelForm, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(this);
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
        if (selectedRow == -1) {
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
        ImageIcon imageIcon = (ImageIcon) productTableModel.getValueAt(selectedRow, 8);

        // Panel de la imagen
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        if (imageIcon != null) {
            Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        } else {
            imageLabel.setText("No hay imagen disponible");
        }

        JPanel imagePanel = new JPanel();
        imagePanel.add(imageLabel);

        // Panel de información con recuadro
        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Detalles del Producto"));

        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(String.valueOf(id)));

        infoPanel.add(new JLabel("Nombre:"));
        infoPanel.add(new JLabel(name));

        infoPanel.add(new JLabel("Precio:"));
        infoPanel.add(new JLabel("$" + price));

        infoPanel.add(new JLabel("Cantidad:"));
        infoPanel.add(new JLabel(String.valueOf(quantity)));

        infoPanel.add(new JLabel("Categoría:"));
        infoPanel.add(new JLabel(category));

        infoPanel.add(new JLabel("Proveedor:"));
        infoPanel.add(new JLabel(supplier));

        infoPanel.add(new JLabel("Estado:"));
        infoPanel.add(new JLabel(status));

        infoPanel.add(new JLabel("Descripción:"));
        infoPanel.add(new JLabel(description));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(imagePanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Crear el cuadro de diálogo
        JDialog dialog = new JDialog(this, "Información de Producto", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Botón de cierre
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(400, 500);
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
                        product.getStatus(), product.getDescription(), obtenerImagenDesdeBytes(product.getImage())};
                rows.add(row);
            }
        }

        public void setProductos(List<Product> productos) {
            this.products = productos;
            rows.clear();
            for(Product product: products) {
                Object[] row = {product.getId(), product.getName(), product.getPrice(), product.getQuantity(), product.getCategory(), product.getSupplier(),
                        product.getStatus(), product.getDescription(), obtenerImagenDesdeBytes(product.getImage())};
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

        private ImageIcon obtenerImagenDesdeBytes(byte[] imageData) {
            if (imageData != null) {
                return new ImageIcon(new ImageIcon(imageData).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
            }
            return null;
        }
    }
}