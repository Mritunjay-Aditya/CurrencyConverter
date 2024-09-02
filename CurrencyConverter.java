import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class CurrencyConverter extends JFrame {
    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JTextField amountField;
    private JLabel resultLabel;
    private Map<String, Double> rates = new HashMap<>();

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";

    CurrencyConverter() {
        setTitle("Currency Converter");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        fromCurrency = new JComboBox<>();
        toCurrency = new JComboBox<>();
        amountField = new JTextField();
        resultLabel = new JLabel("Result: ");

        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });

        add(new JLabel("From:"));
        add(fromCurrency);
        add(new JLabel("To:"));
        add(toCurrency);
        add(new JLabel("Amount:"));
        add(amountField);
        add(convertButton);
        add(resultLabel);

        loadCurrencies();
    }

    private void loadCurrencies() {
        try {
            URI uri = new URI(API_URL);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            JSONObject ratesObject = json.getJSONObject("rates");
            for (String key : ratesObject.keySet()) {
                rates.put(key, ratesObject.getDouble(key));
            }

            fromCurrency.removeAllItems();
            toCurrency.removeAllItems();
            for (String currency : rates.keySet()) {
                fromCurrency.addItem(currency);
                toCurrency.addItem(currency);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Error loading currencies: " + e.getMessage());
        }
    }

    private void convertCurrency() {
        String from = (String) fromCurrency.getSelectedItem();
        String to = (String) toCurrency.getSelectedItem();
        double amount = Double.parseDouble(amountField.getText());

        try {
            URI uri = new URI(API_URL);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(content.toString());
            JSONObject ratesObject = json.getJSONObject("rates");

            double fromRate = ratesObject.getDouble(from);
            double toRate = ratesObject.getDouble(to);

            double result = amount * (toRate / fromRate);

            resultLabel.setText("Result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CurrencyConverter().setVisible(true);
            }
        });

        System.out.println("Currency Converter application started.");
    }
}
