using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;

namespace Entity_Editor.Dialog {
    public class DialogNode : INotifyPropertyChanged {
        public static int idCounter = 0;
        private int _id;
        private string _prompt;
        public ObservableCollection<DialogReply> replies { get; set; }


        public DialogNode() : this(-1) {
        }

        public DialogNode(int id) {
            if (id == -1)
                id = DialogNode.idCounter++;
            if (DialogNode.idCounter <= id)
                DialogNode.idCounter = id + 1;
            this.id = id;

            replies = new ObservableCollection<DialogReply>();
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
