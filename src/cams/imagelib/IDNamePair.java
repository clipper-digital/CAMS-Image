package cams.imagelib;

public class IDNamePair implements Comparable {
  private int mId = -1;
  private String mName = null;

  public IDNamePair() {
  }

  public IDNamePair(int id, String name) {
    mId = id;
    mName = name;
  }

  public String toString() { return mName; }

  public int getId() { return mId; }
  public String getName() { return mName; }

  public void setId(int theValue) { mId = theValue; }
  public void setName(String theValue) { mName = theValue; }

  public int compareTo(Object theObject) {
    return mName.toUpperCase().compareTo(theObject.toString().toUpperCase());
  }

  public boolean equals(Object theObject) {
    return ( (mName.equalsIgnoreCase(((IDNamePair)theObject).getName())) &&
             (mId == ((IDNamePair)theObject).getId()));
  }
}
