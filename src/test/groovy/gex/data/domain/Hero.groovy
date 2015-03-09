package gex.data.domain

import gex.data.pagination.Paginable
import grails.persistence.Entity

/**
 * Created by Tsunllly on 1/26/15.
 */
@Entity
class Hero implements Paginable{

  String id

  String name
  String lastName
  Integer age
  Boolean isImmortal

  static mapping = {
    id generator: 'uuid2'
  }

  static constraints = {
    name blank: false, nullable: false
    lastName nullable: true 
    age nullable: true
    isImmortal nullable: true
  }
}


