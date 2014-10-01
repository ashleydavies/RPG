using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;

namespace Entity_Editor.Dialog {
    public enum DialogActions {
        BOOLDATA_SET,
        INTDATA_INCREASE,
        INTDATA_DECREASE,
        INTDATA_SET,
        CHANGE_NODE,
        END_DIALOG,
    }

    public class DialogAction : INotifyPropertyChanged {
        public static int idCounter = 0;
        private int _id { get; set; }   
        DialogActions _action { get; set; }
        string _args { get; set; }
        ObservableCollection<DialogCondition> _conditions { get; set; }

        public DialogAction() : this(-1) {
        }

        public DialogAction(int id) {
            if (id == -1)
                id = DialogAction.idCounter++;
            if (DialogAction.idCounter <= id)
                DialogAction.idCounter = id + 1;
            this.id = id;

            conditions = new ObservableCollection<DialogCondition>();
        }

        public int id {
            get { return _id; }
            set {
                if (_id != value) {
                    _id = value;
                    NotifyPropertyChanged();
                    NotifyPropertyChanged("stringRepr");
                }
            }
        }
        public DialogActions action {
            get { return _action; }
            set {
                if (_action != value) {
                    _action = value;
                    NotifyPropertyChanged();
                    NotifyPropertyChanged("stringRepr");
                }
            }
        }
        public string args {
            get { return _args; }
            set {
                if (_args != value) {
                    _args = value;
                    NotifyPropertyChanged();
                    NotifyPropertyChanged("stringRepr");
                }
            }
        }
        public ObservableCollection<DialogCondition> conditions {
            get { return _conditions; }
            set {
                if (_conditions != value) {
                    _conditions = value;
                    NotifyPropertyChanged();
                }
            }
        }
        public string stringRepr {
            get { return action.ToString() + "(" + args + ")"; }
        }

        public event PropertyChangedEventHandler PropertyChanged;
        private void NotifyPropertyChanged([CallerMemberName]string propertyName = "") {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (handler != null) {
                handler(this, new PropertyChangedEventArgs(propertyName));
            }
        }
    }
}
