package gex.data

import gex.data.domain.Hero
import gex.data.pagination.PageParams
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

/**
 * Created by Tsunllly on 3/9/15.
 */
@IntegrationTest
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TestApplication)
@Transactional
class PaginableSpec extends Specification {


  def 'Simple pagination works correctly'() {
    setup:
      PageParams pageParams = new PageParams(from: 0, size: 10)
      new Hero(name: 'Mr Incredible').save(failOnError: true, flush: true)
      new Hero(name: 'Superman').save(failOnError: true, flush: true)
      new Hero(name: 'Spiderman').save(failOnError: true,flush: true)
    
    when:
      def resultPage = Hero.searchPage(pageParams)
    
    then:
      resultPage.items
      resultPage.items.size == 3
      resultPage.page == [
        pageTotal:3,
        total:3, 
        from:0, 
        size:10, 
        pageCount:1, 
        currentPage:1, 
        hasNext:false, 
        hasPrev:false
      ]

    cleanup:
      Hero.deleteAll()
  }
  
}
