package io.github.dailystruggle.thethuum.shouts;

public enum ShoutType {
    FUSRODAH(new FusRoDah()),
    YOLTOORSHUL(new YolToorShul()),
    LOKVAHKOOR(new LokVahKoor()),
    KAANDREMOV(new KaanDremOv()),
    WULDNAHKEST(new WuldNahKest()),
    FEIMZIIGRON(new FeimZiiGron()),
    LAASYAHNIR(new LaasYahNir()),
    HUNKAALZOOR(new HunKaalZoor()),
    KRIILUNAUS(new KriiLunAus()),
    STRUNBAHQO(new StrunBahQo());

    public Shout shout;

    ShoutType(Shout shout) {
        this.shout = shout;
    }
}