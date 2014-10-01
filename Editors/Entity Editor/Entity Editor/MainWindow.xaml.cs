using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Entity_Editor {
    public partial class MainWindow : Window {
        public MainWindow() {
            InitializeComponent();
        }

        public string[] modules = { "Dialog" };
        List<Dialog.DialogNode> dialogNodes;

        private void Window_Loaded(object sender, RoutedEventArgs e) {
            // Populate cmbModules
            foreach (string module in modules) {
                cmbModules.Items.Add(module + " module");
            }
        }

        private void btnAddModule_Click(object sender, RoutedEventArgs e) {
            lstModules.Items.Add(cmbModules.SelectedItem);
            cmbModules.Items.Remove(cmbModules.SelectedItem);
        }

        private void btnRemoveModule_Click(object sender, RoutedEventArgs e) {
            cmbModules.Items.Add(lstModules.SelectedItem);
            lstModules.Items.Remove(lstModules.SelectedItem);
        }

        private void btnEditModule_Click(object sender, RoutedEventArgs e) {
            switch ((string) lstModules.SelectedItem) {
                case "Dialog module":
                    DialogEditor dialogEditor = new DialogEditor();
                    dialogEditor.ShowDialog();
                    break;
            }
        }

        private void cmbModules_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            if (cmbModules.SelectedIndex == -1) {
                btnAddModule.IsEnabled = false;
            } else {
                btnAddModule.IsEnabled = true;
            }
        }

        private void lstModules_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            if (lstModules.SelectedIndex == -1) {
                btnEditModule.IsEnabled = false;
                btnRemoveModule.IsEnabled = false;
            } else {
                btnEditModule.IsEnabled = true;
                btnRemoveModule.IsEnabled = true;
            }
        }
    }
}
