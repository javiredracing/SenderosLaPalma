package com.jelcaf.pacomf.patealapalma.binding.dao;

import android.location.Location;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.sql.Date;

/**
 * @author Jorge Carballo
 *         24/03/15
 */
@Table(name = "Photo")
public class Photo extends Model {

   @Column(name = "senderoFK")
   private Sendero sendero;

   private String url;
   private String owner;
   private Integer likes;
   private Location location;

}
