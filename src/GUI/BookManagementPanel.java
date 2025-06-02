package GUI;

import BLL.BookBLL;
import BLL.AuthorBLL;
import BLL.CategoryBLL;
import bll.PublisherBLL;
import Model.Book;
import Model.Author;
import Model.Category;
import Model.Publisher;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class BookManagementPanel extends JPanel {
    private BookBLL bookBLL;
    private AuthorBLL authorBLL;
    private CategoryBLL categoryBLL;
    private PublisherBLL publisherBLL;
    
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    private JTextField txtSearch;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbAuthor;
    private JButton btnSearch;
    
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    
    private DecimalFormat currencyFormat;
    
    public BookManagementPanel() {
        this.bookBLL = new BookBLL();
        this.authorBLL = new AuthorBLL();
        this.categoryBLL = new CategoryBLL();
        this.publisherBLL = new PublisherBLL();
        this.currencyFormat = new DecimalFormat("#,###.## ₫");
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        refreshData();
    }
    
    private void initializeComponents() {
        setBackground(Color.WHITE);
        
        // Search components
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        btnSearch = new JButton("🔍 Tìm");
        btnSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cmbCategory = new JComboBox<>();
        cmbCategory.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbCategory.setPreferredSize(new Dimension(150, 35));
        
        cmbAuthor = new JComboBox<>();
        cmbAuthor.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbAuthor.setPreferredSize(new Dimension(150, 35));
        
        // Add button - Blue color like in the image
        btnAdd = new JButton("📚 Thêm sách mới");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBackground(new Color(0, 123, 255));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Table
        String[] columns = {"ID", "Hình ảnh", "Tên sách", "Tác giả", "Danh mục", "Giá", "Số lượng", "Thao tác"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only action column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class;
                return super.getColumnClass(columnIndex);
            }
        };
        
        bookTable = new JTable(tableModel);
        setupTable();
        
        sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);
    }
    
    private void setupTable() {
        bookTable.setRowHeight(80);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        bookTable.getTableHeader().setBackground(new Color(248, 249, 250));
        bookTable.getTableHeader().setForeground(new Color(73, 80, 87));
        bookTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        bookTable.setSelectionBackground(new Color(220, 240, 255));
        bookTable.setGridColor(new Color(240, 240, 240));
        bookTable.setShowVerticalLines(true);
        bookTable.setShowHorizontalLines(true);
        bookTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Set column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Image
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Title
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Category
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Price
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Stock
        bookTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Actions
        
        // Custom cell renderer for action buttons
        bookTable.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonRenderer());
        bookTable.getColumnModel().getColumn(7).setCellEditor(new ActionButtonEditor());
        
        // Center align ID and Stock columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        bookTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        bookTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        // Right align price column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        bookTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Title and Add button panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("📚 Quản lý sách");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(73, 80, 87));
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(btnAdd, BorderLayout.EAST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        searchPanel.setBackground(Color.WHITE);
        
        // Search input with button
        JPanel searchInputPanel = new JPanel(new BorderLayout());
        searchInputPanel.add(txtSearch, BorderLayout.CENTER);
        searchInputPanel.add(btnSearch, BorderLayout.EAST);
        searchInputPanel.setPreferredSize(new Dimension(350, 35));
        
        searchPanel.add(searchInputPanel);
        searchPanel.add(Box.createHorizontalStrut(20));
        
        // Category dropdown
        JLabel lblCategory = new JLabel("-- Danh mục --");
        lblCategory.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCategory.setForeground(new Color(108, 117, 125));
        searchPanel.add(lblCategory);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(cmbCategory);
        searchPanel.add(Box.createHorizontalStrut(20));
        
        // Author dropdown
        JLabel lblAuthor = new JLabel("-- Tác giả --");
        lblAuthor.setFont(new Font("Arial", Font.PLAIN, 12));
        lblAuthor.setForeground(new Color(108, 117, 125));
        searchPanel.add(lblAuthor);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(cmbAuthor);
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        
        // Create table panel with scroll pane
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add pagination panel at bottom
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton btnPrevious = new JButton("Trước");
        JButton btnPage1 = new JButton("1");
        JButton btnPage2 = new JButton("2");
        JButton btnPage3 = new JButton("3");
        JButton btnNext = new JButton("Sau");
        
        // Style pagination buttons
        JButton[] pageButtons = {btnPrevious, btnPage1, btnPage2, btnPage3, btnNext};
        for (JButton btn : pageButtons) {
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(0, 123, 255));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        // Highlight current page
        btnPage1.setBackground(new Color(0, 123, 255));
        btnPage1.setForeground(Color.WHITE);
        
        paginationPanel.add(btnPrevious);
        paginationPanel.add(btnPage1);
        paginationPanel.add(btnPage2);
        paginationPanel.add(btnPage3);
        paginationPanel.add(btnNext);
        
        add(paginationPanel, BorderLayout.SOUTH);
    }
    
    // Action Button Renderer Class
    private class ActionButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton viewButton, editButton, deleteButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));
            setOpaque(true);
            
            viewButton = createActionButton("👁️", new Color(23, 162, 184));
            editButton = createActionButton("✏️", new Color(255, 193, 7));
            deleteButton = createActionButton("🗑️", new Color(220, 53, 69));
            
            add(viewButton);
            add(editButton);
            add(deleteButton);
        }
        
        private JButton createActionButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 10));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setPreferredSize(new Dimension(25, 25));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }
    
    // Action Button Editor Class
    private class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewButton, editButton, deleteButton;
        private int currentRow;
        
        public ActionButtonEditor() {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 5));
            
            viewButton = createActionButton("👁️", new Color(23, 162, 184));
            editButton = createActionButton("✏️", new Color(255, 193, 7));
            deleteButton = createActionButton("🗑️", new Color(220, 53, 69));
            
            viewButton.addActionListener(e -> viewBook(currentRow));
            editButton.addActionListener(e -> showEditBookDialog(currentRow));
            deleteButton.addActionListener(e -> deleteBook(currentRow));
            
            panel.add(viewButton);
            panel.add(editButton);
            panel.add(deleteButton);
        }
        
        private JButton createActionButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 10));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setPreferredSize(new Dimension(25, 25));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
    
    private void viewBook(int row) {
        // Implementation for viewing book details
        JOptionPane.showMessageDialog(this, "Xem chi tiết sách", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void setupEventListeners() {
        // Search functionality
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        
        // Search button
        btnSearch.addActionListener(e -> filterTable());
        
        // Category filter
        cmbCategory.addActionListener(e -> filterTable());
        
        // Author filter
        cmbAuthor.addActionListener(e -> filterTable());
        
        // Add button
        btnAdd.addActionListener(e -> showAddBookDialog());
    }
    
    private void filterTable() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedCategory = (String) cmbCategory.getSelectedItem();
        String selectedAuthor = (String) cmbAuthor.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String title = entry.getStringValue(2).toLowerCase();
                String author = entry.getStringValue(3);
                String category = entry.getStringValue(4);

                boolean matchesSearch = searchText.isEmpty() || title.contains(searchText);
                boolean matchesCategory = selectedCategory == null || selectedCategory.equals("-- Danh mục --") || category.equals(selectedCategory);
                boolean matchesAuthor = selectedAuthor == null || selectedAuthor.equals("-- Tác giả --") || author.equals(selectedAuthor);

                return matchesSearch && matchesCategory && matchesAuthor;
            }
        };
        sorter.setRowFilter(rf);
    }
    
    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm sách mới", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Form fields
        JTextField txtTitle = new JTextField(20);
        JComboBox<Author> cmbAuthor = new JComboBox<>(authorBLL.getAllAuthors().toArray(new Author[0]));
        JComboBox<Category> cmbCategory = new JComboBox<>(categoryBLL.getAllCategories().toArray(new Category[0]));
        JComboBox<Publisher> cmbPublisher = new JComboBox<>(publisherBLL.getAllPublishers().toArray(new Publisher[0]));
        JTextField txtIsbn = new JTextField(20);
        JTextField txtPrice = new JTextField(20);
        JTextField txtStock = new JTextField(20);
        JTextField txtImageUrl = new JTextField(20);
        JTextArea txtDescription = new JTextArea(3, 20);
        JSpinner spnPublicationDate = new JSpinner(new SpinnerDateModel());
        
        // Add form fields
        addFormField(formPanel, gbc, "Tên sách:", txtTitle);
        addFormField(formPanel, gbc, "Tác giả:", cmbAuthor);
        addFormField(formPanel, gbc, "Danh mục:", cmbCategory);
        addFormField(formPanel, gbc, "Nhà xuất bản:", cmbPublisher);
        addFormField(formPanel, gbc, "ISBN:", txtIsbn);
        addFormField(formPanel, gbc, "Giá:", txtPrice);
        addFormField(formPanel, gbc, "Số lượng:", txtStock);
        addFormField(formPanel, gbc, "URL hình ảnh:", txtImageUrl);
        addFormField(formPanel, gbc, "Ngày xuất bản:", spnPublicationDate);
        addFormField(formPanel, gbc, "Mô tả:", new JScrollPane(txtDescription));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        
        btnSave.addActionListener(e -> {
            try {
                Book book = new Book();
                book.setTitle(txtTitle.getText());
                book.setAuthorId(((Author) cmbAuthor.getSelectedItem()).getAuthorId());
                book.setCategoryId(((Category) cmbCategory.getSelectedItem()).getCategoryId());
                book.setPublisherId(((Publisher) cmbPublisher.getSelectedItem()).getPublisherId());
                book.setIsbn(txtIsbn.getText());
                book.setPrice(new BigDecimal(txtPrice.getText().replaceAll("[^\\d.]", "")));
                book.setStockQuantity(Integer.parseInt(txtStock.getText()));
                book.setImageUrl(txtImageUrl.getText());
                book.setPublicationDate((Date) spnPublicationDate.getValue());
                book.setDescription(txtDescription.getText());
                
                if (bookBLL.addBook(book)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Thêm sách thành công!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Lỗi: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    private void showEditBookDialog(int row) {
        // Similar to showAddBookDialog but pre-fill with book data
        // Implementation details...
        JOptionPane.showMessageDialog(this, "Chức năng sửa sách", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteBook(int row) {
        int bookId = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa cuốn sách này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (bookBLL.deleteBook(bookId)) {
                    JOptionPane.showMessageDialog(this,
                        "Xóa sách thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Clear table
                tableModel.setRowCount(0);
                
                // Load books
                List<Book> books = bookBLL.getAllBooks();
                for (Book book : books) {
                    ImageIcon imageIcon = new ImageIcon(book.getImageUrl());
                    Image image = imageIcon.getImage().getScaledInstance(60, 80, Image.SCALE_SMOOTH);
                    
                    Object[] row = {
                        book.getBookId(),
                        new ImageIcon(image),
                        book.getTitle(),
                        book.getAuthorName(),
                        book.getCategoryName(),
                        currencyFormat.format(book.getPrice()),
                        book.getStockQuantity(),
                        "" // Actions column handled by custom renderer
                    };
                    tableModel.addRow(row);
                }
                
                // Refresh comboboxes
                refreshComboBoxes();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void refreshComboBoxes() {
        // Refresh category combobox
        cmbCategory.removeAllItems();
        cmbCategory.addItem("-- Danh mục --");
        for (Category category : categoryBLL.getAllCategories()) {
            cmbCategory.addItem(category.getName());
        }
        
        // Refresh author combobox
        cmbAuthor.removeAllItems();
        cmbAuthor.addItem("-- Tác giả --");
        for (Author author : authorBLL.getAllAuthors()) {
            cmbAuthor.addItem(author.getName());
        }
    }
    }