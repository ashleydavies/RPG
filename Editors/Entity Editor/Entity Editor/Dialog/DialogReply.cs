using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;

namespace Entity_Editor.Dialog {
    public class DialogReply : INotifyPropertyChanged {
        public static int idCounter = 0;
        private int _id { get; set; }
        private string _prompt { get; set; }
        public ObservableCollection<DialogAction> actions { get; set; }
        public ObservableCollection<DialogCondition> conditions { get; set; }

        public DialogReply() : this(-1) {
        }

        public DialogReply(int id) {
            if (id == -1)
                id = DialogReply.idCounter++;
            if (DialogReply.idCounter <= id)
                DialogReply.idCounter = id + 1;
            this.id = id;
            actions = new ObservableCollection<DialogAction>();
            conditions = new ObservableCollection<DialogCondition>();
        }

        public int id {
            get { return _id; }
            set {
                if (_id != value) {
                    _id = value;
                    NotifyPropertyChanged();
                }
            }
        }
        public string prompt {
            get { return _prompt; }
            set {
                if (_prompt != value) {
                    _prompt = value;
                    NotifyPropertyChanged();
                }
            }
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
