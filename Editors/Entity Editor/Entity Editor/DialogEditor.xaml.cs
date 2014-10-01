using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
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
using System.Windows.Shapes;
using Entity_Editor.Dialog;
using System.Linq;
using System.Xml.Linq;

namespace Entity_Editor {
    /// <summary>
    /// Interaction logic for DialogEditor.xaml
    /// </summary>
    public partial class DialogEditor : Window {
        ObservableCollection<DialogNode> DialogNodes;
        ObservableCollection<DialogCondition> DialogConditions;

        public DialogEditor() {
            InitializeComponent();

            XDocument xDoc = XDocument.Load("1.xml");

            //DialogNodes = new ObservableCollection<DialogNode>();
            DialogConditions = new ObservableCollection<DialogCondition>();

            DialogConditions = new ObservableCollection<DialogCondition>(
                from node in xDoc.Descendants("condition")
                select new DialogCondition(int.Parse(node.Attribute("id").Value)) {
                    condition = (Dialog.DialogConditions)Enum.Parse(typeof(Dialog.DialogConditions), node.Attribute("condition").Value),
                    args = node.Attribute("args").Value
                }
                );
            DialogNodes = new ObservableCollection<DialogNode>(
                from node in xDoc.Descendants("node")
                select new DialogNode(int.Parse(node.Attribute("id").Value)) {
                    prompt = node.Attribute("prompt").Value,
                    replies = new ObservableCollection<DialogReply>(
                        from reply in node.Descendants("reply")
                        select new DialogReply(int.Parse(reply.Attribute("id").Value)) {
                            prompt = reply.Attribute("prompt").Value,
                            actions = new ObservableCollection<DialogAction>(
                                from action in reply.Descendants("action")
                                select new DialogAction(int.Parse(action.Attribute("id").Value)) {
                                    action = (DialogActions)Enum.Parse(typeof(DialogActions), action.Attribute("action").Value),
                                    args = action.Attribute("args").Value,
                                    conditions = new ObservableCollection<DialogCondition>(
                                        from condition in action.Descendants("actionCondition")
                                        select DialogConditions.Where(x => x.id == int.Parse(condition.Attribute("conditionID").Value)).First()
                                    )
                                }
                            ),
                            conditions = new ObservableCollection<DialogCondition>(
                                from condition in reply.Descendants("replyCondition")
                                select DialogConditions.Where(x => x.id == int.Parse(condition.Attribute("conditionID").Value)).First()
                            )
                        }
                        )
                });
        }

        private void Window_Loaded(object sender, RoutedEventArgs e) {
            cmbActions.ItemsSource = Enum.GetValues(typeof(DialogActions));
            cmbConditions.ItemsSource = Enum.GetValues(typeof(DialogConditions));

            lstNodes.ItemsSource = DialogNodes;
            lstConditions.ItemsSource = DialogConditions;
            cmbReplyConditions.ItemsSource = DialogConditions;
            cmbActionConditions.ItemsSource = DialogConditions;

            lstNodes.DisplayMemberPath = "prompt";
            lstReplies.DisplayMemberPath = "prompt";
            lstActions.DisplayMemberPath = "stringRepr";
            lstConditions.DisplayMemberPath = "stringRepr";
            cmbReplyConditions.DisplayMemberPath = "stringRepr";
            cmbActionConditions.DisplayMemberPath = "stringRepr";
            lstReplyConditions.DisplayMemberPath = "stringRepr";
            lstActionConditions.DisplayMemberPath = "stringRepr";
        }

        private void updateForm() {
            // Hide reply information if we've not got a selected node
            bool replyEnable = lstNodes.SelectedIndex != -1;
            btnNodeRemove.IsEnabled = replyEnable;
            txtNodePrompt.IsEnabled = replyEnable;
            btnReplyAdd.IsEnabled = replyEnable;
            cmbReplyConditions.IsEnabled = replyEnable;

            if (replyEnable) {
                lstReplies.ItemsSource = selectedNode().replies;
            } else {
                txtNodePrompt.Clear();
                lstReplies.ItemsSource = null;
            }

            // If we no longer have a selected reply, hide reply information
            bool actionEnable = lstReplies.SelectedIndex != -1;
            btnReplyRemove.IsEnabled = actionEnable;
            txtReplyPrompt.IsEnabled = actionEnable;
            cmbReplyConditions.IsEnabled = actionEnable;
            btnActionAdd.IsEnabled = actionEnable;

            if (actionEnable) {
                lstReplyConditions.ItemsSource = selectedReply().conditions;
                lstActions.ItemsSource = selectedReply().actions;
            } else {
                cmbReplyConditions.SelectedIndex = -1;
                txtReplyPrompt.Clear();
                lstActions.ItemsSource = null;
            }

            // If we no longer have a selected action, hide action information
            bool actionSelected = lstActions.SelectedIndex != -1;
            btnActionRemove.IsEnabled = actionSelected;
            cmbActions.IsEnabled = actionSelected;
            txtActionArguments.IsEnabled = actionSelected;
            cmbActionConditions.IsEnabled = actionSelected;

            if (actionSelected) {
                lstActionConditions.ItemsSource = selectedAction().conditions;
                cmbActions.SelectedItem = selectedAction().action;
                txtActionArguments.Text = selectedAction().args;
            } else {
                cmbActions.SelectedIndex = -1;
                txtActionArguments.Clear();
            }

            // Check if a condition is selected
            bool conditionAccepted = lstConditions.SelectedIndex != -1;
            cmbConditions.IsEnabled = conditionAccepted;
            txtConditionArguments.IsEnabled = conditionAccepted;

            if (conditionAccepted) {
                cmbConditions.SelectedItem = selectedCondition().condition;
                txtConditionArguments.Text = selectedCondition().args;
            } else {
                cmbConditions.SelectedIndex = -1;
                txtConditionArguments.Clear();
            }


            // Handle the reply/action conditions
            btnReplyConditionAdd.IsEnabled = cmbReplyConditions.SelectedIndex != -1;
            btnActionConditionAdd.IsEnabled = cmbActionConditions.SelectedIndex != -1;

            btnReplyConditionRemove.IsEnabled = lstReplyConditions.SelectedIndex != -1;
            btnActionConditionRemove.IsEnabled = lstActionConditions.SelectedIndex != -1;
        }



        private void btnNodeAdd_Click(object sender, RoutedEventArgs e) {
            DialogNodes.Add(new DialogNode() { prompt = "New Dialog Node" });
        }

        private void btnNodeRemove_Click(object sender, RoutedEventArgs e) {
            DialogNodes.Remove(selectedNode());
            updateForm();
        }

        private void lstNodes_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            if (lstNodes.SelectedIndex != -1)
                txtNodePrompt.Text = selectedNode().prompt;

            updateForm();
        }

        private void txtNodePrompt_TextChanged(object sender, TextChangedEventArgs e) {
            if (lstNodes.SelectedIndex != -1)
                selectedNode().prompt = txtNodePrompt.Text;
        }



        private void btnReplyAdd_Click(object sender, RoutedEventArgs e) {
            selectedNode().replies.Add(new DialogReply() { prompt = "New Dialog Reply" });
        }

        private void btnReplyRemove_Click(object sender, RoutedEventArgs e) {
            selectedNode().replies.Remove(selectedReply());
            updateForm();
        }

        private void lstReplies_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            if (lstReplies.SelectedIndex != -1)
                txtReplyPrompt.Text = selectedReply().prompt;
            updateForm();
        }

        private void txtReplyPrompt_TextChanged(object sender, TextChangedEventArgs e) {
            if (lstReplies.SelectedIndex != -1)
                selectedReply().prompt = txtReplyPrompt.Text;
        }



        private void btnActionAdd_Click(object sender, RoutedEventArgs e) {
            selectedReply().actions.Add(new DialogAction() { action = DialogActions.BOOLDATA_SET, args = "false" });
        }

        private void btnActionRemove_Click(object sender, RoutedEventArgs e) {
            selectedReply().actions.Remove(selectedAction());
            updateForm();
        }

        private void lstActions_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            updateForm();
        }

        private void cmbActions_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            if (lstActions.SelectedIndex != -1)
                selectedAction().action = (DialogActions)cmbActions.SelectedItem;
        }

        private void txtActionArguments_TextChanged(object sender, TextChangedEventArgs e) {
            if (lstActions.SelectedIndex != -1)
                selectedAction().args = txtActionArguments.Text;
        }



        private void btnConditionAdd_Click(object sender, RoutedEventArgs e) {
            DialogConditions.Add(new DialogCondition() { condition = Dialog.DialogConditions.BOOLDATA_EQUALS, args = "false" });
        }

        private void lstConditions_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            updateForm();
        }

        private void cmbConditions_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            selectedCondition().condition = (Dialog.DialogConditions)cmbConditions.SelectedItem;
        }

        private void txtConditionArguments_TextChanged(object sender, TextChangedEventArgs e) {
            selectedCondition().args = txtConditionArguments.Text;
        }



        private void cmbReplyConditions_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            updateForm();
        }

        private void cmbActionConditions_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            updateForm();
        }



        private void btnReplyConditionRemove_Click(object sender, RoutedEventArgs e) {
            selectedReply().conditions.Remove((DialogCondition)lstReplyConditions.SelectedItem);
        }

        private void btnActionConditionRemove_Click(object sender, RoutedEventArgs e) {
            selectedAction().conditions.Remove((DialogCondition)lstActionConditions.SelectedItem);
        }

        private void lstReplyConditions_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            updateForm();
        }

        private void lstActionConditions_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            updateForm();
        }

        private void btnReplyConditionAdd_Click(object sender, RoutedEventArgs e) {
            selectedReply().conditions.Add((DialogCondition)cmbReplyConditions.SelectedItem);
        }

        private void btnActionConditionAdd_Click(object sender, RoutedEventArgs e) {
            selectedAction().conditions.Add((DialogCondition)cmbActionConditions.SelectedItem);
        }





        public DialogNode selectedNode() {
            return (DialogNode)lstNodes.SelectedItem;
        }

        public DialogReply selectedReply() {
            return (DialogReply)lstReplies.SelectedItem;
        }

        public DialogAction selectedAction() {
            return (DialogAction)lstActions.SelectedItem;
        }

        public DialogCondition selectedCondition() {
            return (DialogCondition)lstConditions.SelectedItem;
        }
    }
}
