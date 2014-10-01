using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;

namespace Entity_Editor.Dialog {
    public enum DialogConditions {
        BOOLDATA_EQUALS,
        INTDATA_EQUALS,
        INTDATA_MORETHAN,
        INTDATA_LESSTHAN,
    }

    public class DialogCondition : INotifyPropertyChanged {
        public static int idCounter = 0;
        int _id { get; set; }
        DialogConditions _condition { get; set; }
        string _args { get; set; }

        public DialogCondition() : this(-1) {
        }

        public DialogCondition(int id) {
            if (id == -1)
                id = DialogCondition.idCounter++;
            if (DialogCondition.idCounter <= id)
                DialogCondition.idCounter = id + 1;
            this.id = id;
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
        public DialogConditions condition {
            get { return _condition; }
            set {
                if (_condition != value) {
                    _condition = value;
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
        public string stringRepr {
            get { return condition.ToString() + "(" + args + ")"; }
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
